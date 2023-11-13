package iobb.kagetorya.shinobigami;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShinobigamiUtils {
    // アイテムName変更
    public static void setItemName(ItemStack is, String name){
        if(is.getItemMeta() == null){
            return;
        }
        is.getItemMeta().setDisplayName(name);
    }

    // アイテムLore変更
    public static void setItemLore(ItemStack is, List<String> lores){
        if(is.getItemMeta() == null){
            return;
        }
        is.getItemMeta().setLore(lores);
    }

    // アイテムスタックの簡易一括変更
    public static ItemStack makeItemStack(ItemStack is, String name, String... lore){
        if(is.getItemMeta() == null){
            return is;
        }

        List<String> list = new ArrayList<>(Arrays.asList(lore));
        setItemName(is,name);
        setItemLore(is,list);
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
}
