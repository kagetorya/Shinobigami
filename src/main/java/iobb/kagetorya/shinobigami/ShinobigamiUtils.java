package iobb.kagetorya.shinobigami;

import org.bukkit.inventory.ItemStack;

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
}
