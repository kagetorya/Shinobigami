package iobb.kagetorya.shinobigami;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShinobigamiUtils {
    // アイテムName変更
    public static ItemStack setItemName(ItemStack is, String name){
        if(is.getItemMeta() == null){
            return null;
        }
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return is;
    }

    // アイテムLore変更
    public static ItemStack setItemLore(ItemStack is, List<String> lores){
        if(is == null || is.getItemMeta() == null){
            return is;
        }
        ItemMeta im = is.getItemMeta();
        im.setLore(lores);
        is.setItemMeta(im);
        return is;
    }

    // アイテムスタックの簡易一括変更
    public static ItemStack makeItemStack(ItemStack is, String name, String... lore){
        if(is == null || is.getItemMeta() == null){
            return is;
        }

        List<String> list = new ArrayList<>(Arrays.asList(lore));
        is = setItemName(is,name);
        is = setItemLore(is,list);
        return is;
    }
    public static List<String> getCharacterIDs(Player p) {
        File sheetFolder = new File(Shinobigami.getInstance().getDataFolder(),"Sheets");
        File playerFolder = new File(sheetFolder, p.getUniqueId().toString());
        List<String> characters = new ArrayList<>();

        // 例外処理
        if (!(playerFolder.exists())) {
            p.sendMessage("Folder not found");
            return null;
        }

        // ファイル群から名前を抽出しリスト化
        File[] files = playerFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        // 例外処理
        if(files == null){
            return null;
        }
        // List化
        for (File file : files) {
            String fileName = file.getName();
            String character = fileName.substring(0, fileName.length() - 4);
            characters.add(character);
        }
        return characters;
    }

    public static File getSheetPath(Player p,String id){
        File sheetFolder = new File(Shinobigami.getInstance().getDataFolder(),"Sheets");
        File playerFolder = new File(sheetFolder, p.getUniqueId().toString());

        // 例外処理(プレイヤー別ディレクトリの生成)
        if(!playerFolder.exists()){
            if(playerFolder.mkdirs()){
                p.sendMessage("§5§lS§dhinobi §7》§fプレイヤー別シート管理フォルダを生成しました。");
            } else {
                p.sendMessage("§5§lS§dhinobi §7》§f§cプレイヤー別シート管理フォルダの生成に失敗しました。");
            }
        }

        return new File(playerFolder,id+".yml");
    }

    public static void setCharacterInfo(Player p,String id,String tag,String cont){
        File sheet = getSheetPath(p,id);
        // idの例外処理
        if(!(sheet.exists())){
            p.sendMessage("§cError: そのIDのキャラクターシートは存在しません");
            return;
        }
        // キャラクター名の保存
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        cfg.set(tag, cont);
        try{
            cfg.save(sheet);
        } catch (IOException e){
            e.printStackTrace();
        }

        // 処理報告とエフェクト
        p.sendMessage("§5§lS§dhinobi §7》データが設定されました。§7id: "+id);
        p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
    }
}
