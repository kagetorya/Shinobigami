package iobb.kagetorya.shinobigami;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ShinobigamiCommands implements TabExecutor {
    private final String prefix = "§5§lS§dhinobi §7》§f";

    private final File sheetFolder = new File(Shinobigami.getInstance().getDataFolder(),"Sheets");

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String... args){
        // プレイヤー判定
        if ( !(s instanceof Player) ){
            s.sendMessage("§cError: コマンド実行者はプレイヤーではありません。");
            return true;
        }
        Player p = (Player) s;

        if (cmd.getName().equalsIgnoreCase("shinobigami")){
            if(args.length == 0){
                return false;
            }
            // キャラクターシート管理コマンド
            if(args[0].equalsIgnoreCase("charactersheet")){
                if(args.length == 1){
                    return false;
                }

                // ディレクトリとシートの作成
                if(args[1].equalsIgnoreCase("make")){
                    if(args.length == 2){
                        p.sendMessage("§cError: キャラクターシートのIDを入力してください");
                        return true;
                    }
                    return makeCharacterSheet(p,args[2]);
                }
                // シートの削除
                else if(args[1].equalsIgnoreCase("delete")){
                    if(args.length == 2){
                        p.sendMessage("§cError: キャラクターシートのIDを入力してください");
                        return true;
                    }
                    return deleteCharacterSheet(p,args[2]);
                }
                // キャラクターの名前の設定
                else if(args[1].equalsIgnoreCase("name")){
                    if(args.length == 2){
                        p.sendMessage("§cError: キャラクターシートのIDを入力してください");
                        return true;
                    }
                    //キャラクター名の例外処理
                    if(args.length == 3){
                        p.sendMessage("§cError: キャラクターの名前を入力してください");
                        return true;
                    }
                    return setCharacterName(p,args[2],args[3]);
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String... args){

        // プレイヤー判定
        if (!(s instanceof Player) ){
            return null;
        }
        Player p = (Player) s;

        // コマンドのTAB補完 [/Shinobigami]
        if(cmd.getName().equalsIgnoreCase("Shinobigami")) {

            // args[0]の補完
            if (args.length == 1) {
                String str = args[0].toLowerCase();
                ArrayList<String> candidates = new ArrayList<>();
                for (String list : new String[]{"charactersheet"}) {
                    if (list.startsWith(str)) {
                        candidates.add(list);
                    }
                }
                return candidates;
            }
            // args[1]の補完
            else if(args.length == 2){

                // キャラクターシート管理での補完 [/Shinobigami charactersheet]
                if(args[0].equalsIgnoreCase("charactersheet")) {
                    String str = args[1].toLowerCase();
                    ArrayList<String> candidates = new ArrayList<>();
                    for (String list : new String[]{"make", "delete", "name"}) {
                        if (list.startsWith(str)) {
                            candidates.add(list);
                        }
                    }
                    return candidates;
                }
            }
            // args[2]の補完
            else if(args.length == 3){
                // キャラクターシート管理での補完 [/Shinobigami charactersheet (delete/name)]
                if(args[0].equalsIgnoreCase("charactersheet")) {
                    if(args[1].matches("(?i)(delete|name)")) {
                        return getCharacters(p);
                    }
                }


            }
        }
        return null;
    }

    public boolean makeCharacterSheet(Player p,String id){
        File playerFolder = new File(sheetFolder,p.getUniqueId().toString());
        // 例外処理
        // プレイヤー別ディレクトリの生成
        if(!playerFolder.exists()){
            if(playerFolder.mkdirs()){
                p.sendMessage(prefix+"プレイヤー別シート管理フォルダを生成しました。");
            } else {
                p.sendMessage(prefix+"§cプレイヤー別シート管理フォルダの生成に失敗しました。");
            }
        }
        // シート保存ファイルの生成
        File sheet = new File(playerFolder ,id+".yml");
        if (!sheet.exists()){
            try {
                if(sheet.createNewFile()){
                    p.sendMessage(prefix+"キャラクターシート保存用ファイルを生成しました。§7id: "+id);

                } else {
                    p.sendMessage(prefix+"§cプレイヤー別シート管理フォルダの生成に失敗しました。");
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        } else {
            p.sendMessage(prefix+"そのIDは既に存在しています。§7id: "+id);
        }
        return true;
    }

    public boolean deleteCharacterSheet(Player p,String id){
        File playerFolder = new File(sheetFolder,p.getUniqueId().toString());
        // 例外処理
        File sheet = new File(playerFolder,id+".yml");
        if(!(sheet.exists())){
            p.sendMessage("§cError: そのIDのキャラクターシートは存在しません");
            return false;
        }

        // シート保存ファイルの削除
        if(sheet.delete()){
            p.sendMessage(prefix+"キャラクターシートが削除されました。§7id: "+id);
        } else {
            p.sendMessage(prefix+"キャラクターシートの削除に失敗しました。§7id: "+id);
        }
        return true;
    }

    public boolean setCharacterName(Player p,String id,String name){
        File playerFolder = new File(sheetFolder,p.getUniqueId().toString());
        File sheet = new File(playerFolder,id+".yml");
        // idの例外処理
        if(!(sheet.exists())){
            p.sendMessage("§cError: そのIDのキャラクターシートは存在しません");
            return false;
        }
        // キャラクター名の保存
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        cfg.set("name", name);
        try{
            cfg.save(sheet);
        } catch (IOException e){
            e.printStackTrace();
        }

        // 処理報告とエフェクト
        p.sendMessage(prefix+"キャラクター名が["+name+"]に設定されました。§7id: "+id);
        p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
        return true;
    }

    public List<String> getCharacters(Player p) {
        File playerFolder = new File(sheetFolder, p.getUniqueId().toString());
        List<String> characters = new ArrayList<>();

        // 例外処理
        if (!(playerFolder.exists())) {
            p.sendMessage("Folder not found");
            return null;
        }

        // ファイル群から名前を抽出しリスト化
        File[] files = playerFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        for (File file : files) {
            String fileName = file.getName();
            String character = fileName.substring(0, fileName.length() - 4);
            characters.add(character);
        }
        return characters;
    }


}