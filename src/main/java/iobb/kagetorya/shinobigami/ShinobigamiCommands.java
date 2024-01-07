package iobb.kagetorya.shinobigami;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static iobb.kagetorya.shinobigami.ShinobigamiGUI.openGUI;
import static iobb.kagetorya.shinobigami.ShinobigamiPoints.addPoint;
import static iobb.kagetorya.shinobigami.ShinobigamiUtils.*;
import static iobb.kagetorya.shinobigami.ShinobigamiSkillManager.*;
import static iobb.kagetorya.shinobigami.ShinobigamiTables.prefix;


@SuppressWarnings("NullableProblems")
public class ShinobigamiCommands implements TabExecutor {


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
                openGUI(p,"terminal");
                return true;
            }
            // キャラクターシート管理コマンド
            if(args[0].equalsIgnoreCase("charactersheet")){
                if(args.length == 1){
                    return false;
                }

                // ディレクトリとシートの作成
                if(args[1].equalsIgnoreCase("make")){
                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,null)){
                        return true;
                    }
                    return makeCharacterSheet(p,args[2]);
                }
                // シートの削除
                else if(args[1].equalsIgnoreCase("delete")){
                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,null)){
                        return true;
                    }
                    return deleteCharacterSheet(p,args[2]);
                }
                // キャラクターの名前の設定
                else if(args[1].equalsIgnoreCase("name")){
                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,"キャラクターの名前")){
                        return true;
                    }
                    // シートへの書き込み
                    setSheet(p,args[2],"name",args[3]);
                    return true;
                }
                // キャラクターの背景の設定
                else if(args[1].equalsIgnoreCase("back")){
                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,"キャラクターの背景情報")){
                        return true;
                    }
                    // argsの形式を変換しシートへ書き込み
                    String lore = String.join("|",new ArrayList<>(Arrays.asList(args)).subList(3,args.length));
                    lore = lore.replace("&","§");
                    setSheet(p,args[2],"back",lore);
                    return true;
                }
                // キャラクターの流派の設定
                else if(args[1].equalsIgnoreCase("style")){
                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,"流派")){
                        return true;
                    }
                    // 入力形式の例外処理
                    if(!args[3].matches("(?i)(hasuba|kurama|hagure|hirasaka|otogi|oni)")){
                        p.sendMessage("§cError: 流派の入力形式が違います");
                        return true;
                    }

                    // シートへの書き込み
                    setSheet(p,args[2],"style",args[3]);
                    return true;
                }
                // キャラクターの階級の設定
                else if(args[1].equalsIgnoreCase("rank")){
                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,"階級")){
                        return true;
                    }
                    // 入力形式の例外処理
                    if(!args[3].matches("(?i)(grass|low|lowLeader|mid|midLeader|high|highLeader|head)")){
                        p.sendMessage("§cError: 階級の入力形式が違います");
                        return true;
                    }

                    // シートへの書き込み
                    setSheet(p,args[2],"rank",args[3]);
                    return true;
                }
                // キャラクターの特技の設定
                else if(args[1].equalsIgnoreCase("skill")){

                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,"特技")){
                        return true;
                    }

                    // 最新削除
                    if(args[3].equalsIgnoreCase("delete")){
                        removeSkill(p,args[2],null);
                        return true;
                    }

                    // 特技設定処理
                    editSkill(p,args[2],args[3]);
                    return true;

                }
                // キャラクターの功績点の設定
                else if(args[1].equalsIgnoreCase("point")){
                    // コマンド入力の例外処理
                    if(checkCommandArgs(p,args,"功績店")){
                        return true;
                    }

                    // ポイント処理
                    try {
                        int point = Integer.parseInt(args[3]);
                        addPoint(p,args[2],point);
                        return true;
                    } catch (NumberFormatException e) {
                       p.sendMessage(prefix+"§c数値を入力してください。 §8入力値:"+args[3]);
                       return true;
                    }
                }

            }else if (args[0].equalsIgnoreCase("GUI")){
                if(args[2].equalsIgnoreCase("skill")){
                    openSkillMenu(p,args[1]);
                    return true;
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
                for (String list : new String[]{"charactersheet","GUI"}) {
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
                    for (String list : new String[]{"make", "delete", "name", "back", "style", "rank", "skill", "point"}) {
                        if (list.startsWith(str)) {
                            candidates.add(list);
                        }
                    }
                    return candidates;
                }

                // GUIでの補完 [/Shinobigami GUI]
                if(args[0].equalsIgnoreCase("GUI")) {
                    String str = args[1].toLowerCase();
                    ArrayList<String> candidates = new ArrayList<>();
                    for (String list : new String[]{"skill"}) {
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
                    if(args[1].matches("(?i)(delete|name|back|style)")) {
                        return getCharacterIDs(p);
                    }
                }
            }
            // args[3]の補完
            else if(args.length == 4){
                // キャラクターシート管理での補完 [/Shinobigami charactersheet (delete/name)]
                if(args[0].equalsIgnoreCase("charactersheet")) {
                    if(args[1].matches("(?i)(style)")){
                        String str = args[1].toLowerCase();
                        ArrayList<String> candidates = new ArrayList<>();
                        for (String list : new String[]{"hasuba", "kurama", "hagure", "hirasaka", "otogi","oni"}) {
                            if (list.startsWith(str)) {
                                candidates.add(list);
                            }
                        }
                        return candidates;
                    }
                }
            }
        }
        return null;
    }

    public boolean makeCharacterSheet(Player p,String id){
        File sheet = getSheetPath(p,id);
        // 最大シート保持数の確認
        if(getCharacterIDs(p).size() >= 18){
            p.sendMessage(prefix+"§cキャラクターシートの所持数が最大です。どれかを消してから実行してください。");
            return true;
        }
        // シート保存ファイルの生成
        if (!sheet.exists()){
            try {
                if(sheet.createNewFile()){
                    p.sendMessage(prefix+"キャラクターシート保存用ファイルを生成しました。§7id: "+id);
                    setSheet(p,id,"rank","mid");
                    setSheet(p,id,"points","0");
                } else {
                    p.sendMessage(prefix+"§cキャラクターシート保存用ファイルの生成に失敗しました。");
                }
            }catch(IOException e){
                logger.log(Level.SEVERE,"couldn't make file");
            }
        } else {
            p.sendMessage(prefix+"そのIDは既に存在しています。§7id: "+id);
        }
        return true;
    }

    public boolean deleteCharacterSheet(Player p,String id){
        // 例外処理
        File sheet = getSheetPath(p,id);
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

    public boolean checkCommandArgs(Player p,String[] args,String contentName){
        // IDの例外処理
        if(args.length == 2){
            p.sendMessage("§cError: キャラクターシートのIDを入力してください");
            return true;
        }
        //　特技の例外処理
        if(args.length == 3 && contentName != null){
            p.sendMessage("§cError: "+contentName+"を入力してください");
            return true;
        }
        return false;
    }
}
