package iobb.kagetorya.shinobigami;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static iobb.kagetorya.shinobigami.ShinobigamiSkillManager.openSkillMenu;
import static iobb.kagetorya.shinobigami.ShinobigamiTables.*;
import static iobb.kagetorya.shinobigami.ShinobigamiUtils.*;

public class ShinobigamiGUI implements Listener {

    public static void openGUI(Player p,String guiName) {

        // ターミナル(プレイヤー用)
        if (guiName.equalsIgnoreCase("terminal")) {
            openTerminal(p);
        }
        // キャラクターシート管理GUI
        else if(guiName.equalsIgnoreCase("sheetSelector")) {
            openSheetSelector(p);
        }
        // 指定キャラクターの編集
        else if(guiName.startsWith("sheetEditor_id:")){
            openSheetEditor(p,getID(guiName));
        }
        // 流派選択GUI
        else if(guiName.startsWith("styleSelector_id:")){
            openStyleSelector(p,getID(guiName));
        }
        // 階級選択GUI
        else if(guiName.startsWith("rankSelector_id:")){
            openRankSelector(p,getID(guiName));
        }
        // 忍法選択GUI
        else if(guiName.startsWith("artsSelector_id:")){
            openArtSlotSelector(p,getID(guiName));
        }

        // 例外処理
        else {
            p.sendMessage("§cError: そのGUIは存在しません。");
        }
        // GUIが開かれる音
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
    }

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e){
        if(e.getView().getTitle().equals(guiTitles.get("terminal"))){
            clickedTerminal(e);
        } else if(e.getView().getTitle().equals(guiTitles.get("sheetSelector"))){
            clickedSheetSelector(e);
        } else if (e.getView().getTitle().startsWith(guiTitles.get("sheetEditor"))) {
            clickedCharacterEditor(e);
        } else if(e.getView().getTitle().startsWith(guiTitles.get("styleSelector"))){
            clickedStyleSelector(e);
        } else if(e.getView().getTitle().startsWith(guiTitles.get("rankSelector"))){
            clickedRankSelector(e);
        } else if(e.getView().getTitle().startsWith(guiTitles.get("artsSlotSelector"))){
            clickedArtSlotSelector(e);
        } else if(e.getView().getTitle().startsWith(guiTitles.get("artsSelector"))){
            clickedArtsSelector(e);
        }
    }

    // ターミナル
    public static void openTerminal(Player p){
        Inventory inv = Bukkit.createInventory(null,27,guiTitles.get("terminal"));

        // GUI装飾配置
        for (int num = 0; num < 9; num++) {
            inv.setItem(num,guiItems.get("blackPane"));
            inv.setItem(num+18,guiItems.get("blackPane"));
        }
        inv.setItem(9,guiItems.get("ironBars"));
        inv.setItem(17,guiItems.get("ironBars"));

        // ギミックアイテム
        inv.setItem(11,guiItems.get("terminal_sheetSelector"));

        // GUIの表示
        p.openInventory(inv);
    }
    public static void clickedTerminal(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }

        // 持ち出し防止
        e.setCancelled(true);

        // ギミックアイテム
        if(e.getCurrentItem().equals(guiItems.get("terminal_sheetSelector"))){
            openGUI(p,"sheetSelector");
        }
    }

    // シートセレクター
    public static void openSheetSelector(Player p){
        Inventory inv = Bukkit.createInventory(null, 27, guiTitles.get("sheetSelector"));

        // GUI装飾配置
        for (int num = 0; num < 9; num++)
            inv.setItem(num, guiItems.get("lightBluePane"));

        // シートが存在しない場合
        if (getCharacterIDs(p).isEmpty()) {
            inv.setItem(13, guiItems.get("sheetSelector_empty"));
            p.openInventory(inv);
            return;
        }

        // キャラクターシートの一覧表示
        int slot = 9;
        for(String id : getCharacterIDs(p)){
            File sheet = getSheetPath(p,id);
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
            if(cfg.get("name") == null){
                inv.setItem(slot,makeItemStack(new ItemStack(Material.PAPER),"§b"+id,"§f名前: §c未設定"," ","§f以下のコマンドで名前を設定できます。","§f[/shinobi charactersheet name "+id+" (好きな名前)]"));
            } else {
                inv.setItem(slot,makeItemStack(new ItemStack(Material.PAPER),"§b"+id,"§f名前: "+cfg.get("name")," ","§fクリックで詳細設定へ移動できます。"));
            }
            slot ++;
        }

        // 戻るアイテム
        inv.setItem(26,guiItems.get("terminal_back"));
        // GUIの表示
        p.openInventory(inv);
    }
    public static void clickedSheetSelector(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        //持ち出し防止
        e.setCancelled(true);

        // シート選択
        if(e.getCurrentItem().getType().equals(Material.PAPER)){
            String id = e.getCurrentItem().getItemMeta().getDisplayName().replace("§b","");
            openGUI(p,"sheetEditor_id:"+id);
        }

        // 前のGUIへ戻る
        else if (e.getCurrentItem().equals(guiItems.get("terminal_back"))){
            openGUI(p,"terminal");
        }

    }

    // シートエディター
    public static void openSheetEditor(Player p,String id){
        Inventory inv = Bukkit.createInventory(null,27,guiTitles.get("sheetEditor")+" §7id:"+id);
        File sheet = getSheetPath(p,id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);

        // GUI装飾配置
        for (int num = 0; num < 9; num++)
            inv.setItem(num, guiItems.get("purplePane"));

        // 名前と背景情報
        String name = cfg.getString("name");
        if(name == null){
            name = "§c名前は未設定です。";
        }
        String[] back;
        String str = cfg.getString("back");
        if(str == null){
            back = new String[]{"§c背景情報は未設定です。", "§f以下のコマンドで[名前/背景情報]を設定できます。", "§f[/shinobi charactersheet [name/back] " + id + " (好きな内容)]"};
        } else {
            back = str.split("\\|");
        }
        inv.setItem(10,makeItemStack(new ItemStack(Material.NAME_TAG),name,back));

        // 流派情報
        if(cfg.get("style") == null){
            inv.setItem(12,guiItems.get("sheetEditor_defStyle"));
        } else {
            inv.setItem(12, guiItems.get("styleSelector_"+cfg.getString("style")));
        }

        // 階級情報
        if(cfg.get("rank") == null){
            inv.setItem(14,guiItems.get("sheetEditor_defRank"));
        } else {
            inv.setItem(14, guiItems.get("rankSelector_"+cfg.getString("rank")));
        }

        // 特技情報
        inv.setItem(16,makeItemStack(new ItemStack(Material.REDSTONE),"§9特技","§fクリックで設定できます。"));

        // 忍法情報
        inv.setItem(19,makeItemStack(new ItemStack(Material.MOJANG_BANNER_PATTERN),"§5忍法","§fクリックで設定できます。"));

        // 戻るアイテム
        inv.setItem(26,guiItems.get("terminal_back"));

        p.openInventory(inv);
    }
    public static void clickedCharacterEditor(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        // 持ち出し防止
        e.setCancelled(true);

        // 編集対象選択
        String id = getID(e.getView().getTitle());
        if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§d流派§7:")){
            openGUI(p,"styleSelector_id:"+id);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a階級§7:")) {
            openGUI(p,"rankSelector_id:"+id);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§9特技")){
            openSkillMenu(p,id);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§5忍法")) {
            openGUI(p,"artsSelector_id:"+id);
        }
        // 前のGUIへ戻る
        else if (e.getCurrentItem().equals(guiItems.get("terminal_back"))){
            openGUI(p,"sheetSelector");
        }
    }

    // 流派セレクター
    public static void openStyleSelector(Player p,String id){
        File sheet = getSheetPath(p,id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        ItemStack item;
        Inventory inv = Bukkit.createInventory(null,27,guiTitles.get("styleSelector")+" §7id:"+id);

        // 装飾配置
        for (int num = 0; num < 9; num++)
            inv.setItem(num, guiItems.get("redPane"));

        // ギミックアイテム
        inv.setItem(11,guiItems.get("styleSelector_hasuba"));
        inv.setItem(13,guiItems.get("styleSelector_kurama"));
        inv.setItem(15,guiItems.get("styleSelector_hagure"));
        inv.setItem(20,guiItems.get("styleSelector_hirasaka"));
        inv.setItem(22,guiItems.get("styleSelector_otogi"));
        inv.setItem(24,guiItems.get("styleSelector_oni"));

        if(cfg.get("style") != null && guiItems.get("styleSelector_"+cfg.get("style")) != null) {
            for (int i = 9; i < 27; i++) {
                item = inv.getItem(i);
                if (item != null && item.isSimilar(guiItems.get("styleSelector_" + cfg.get("style")))) {
                    inv.setItem(i, makeEnchantedItemStack(inv.getItem(i)));
                    break;
                }
            }
        }

        // 戻るアイテム
        inv.setItem(26,guiItems.get("terminal_back"));
        // GUI表示
        p.openInventory(inv);
    }
    public static void clickedStyleSelector(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        // 持ち出し防止
        e.setCancelled(true);
        // id取得
        String id = getID(e.getView().getTitle());

        // 流派選択
        if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§d流派§7:")){
            List<String> lores = e.getCurrentItem().getItemMeta().getLore();
            if(lores == null){
                return;
            }
            String cont = getID(lores.get(lores.size()-1));

            setSheet(p,id,"style",cont);
            openGUI(p,"sheetEditor_id:"+id);
        }
        // 前のGUIへ戻る
        else if (e.getCurrentItem().equals(guiItems.get("terminal_back"))){
            openGUI(p,"sheetEditor_id:"+id);
        }
    }

    // 階級選択GUI
    public static void openRankSelector(Player p,String id){
        File sheet = getSheetPath(p,id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        ItemStack item;
        Inventory inv = Bukkit.createInventory(null,27,guiTitles.get("rankSelector")+" §7id:"+id);

        // 装飾配置
        for (int num = 0; num < 9; num++)
            inv.setItem(num, guiItems.get("greenPane"));

        // ギミックアイテム
        inv.setItem(10,guiItems.get("rankSelector_grass"));
        inv.setItem(12,guiItems.get("rankSelector_low"));
        inv.setItem(14,guiItems.get("rankSelector_lowLeader"));
        inv.setItem(16,guiItems.get("rankSelector_mid"));
        inv.setItem(19,guiItems.get("rankSelector_midLeader"));
        inv.setItem(21,guiItems.get("rankSelector_high"));
        inv.setItem(23,guiItems.get("rankSelector_highLeader"));
        inv.setItem(25,guiItems.get("rankSelector_head"));

        if(cfg.get("style") != null && guiItems.get("rankSelector_"+cfg.get("style")) != null) {
            for (int i = 9; i < 27; i++) {
                item = inv.getItem(i);
                if (item != null && item.isSimilar(guiItems.get("rankSelector_" + cfg.get("style")))) {
                    inv.setItem(i, makeEnchantedItemStack(inv.getItem(i)));
                    p.sendMessage("slot:" + i);
                    break;
                }
            }
        }

        // 戻るアイテム
        inv.setItem(26,guiItems.get("terminal_back"));
        // GUI表示
        p.openInventory(inv);
    }
    public static void clickedRankSelector(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        // 持ち出し防止
        e.setCancelled(true);
        // ID取得
        String id = getID(e.getView().getTitle());

        // 階級選択
        if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a階級§7:")){
            List<String> lores = e.getCurrentItem().getItemMeta().getLore();
            if(lores == null){
                return;
            }
            String cont = getID(lores.get(lores.size()-1));

            setSheet(p,id,"rank",cont);
            openGUI(p,"sheetEditor_id:"+id);
        }
        // 前のGUIへ戻る
        else if (e.getCurrentItem().equals(guiItems.get("terminal_back"))){
            openGUI(p,"sheetEditor_id:"+id);
        }
    }

    // 忍法スロットGUI
    public static void openArtSlotSelector(Player p,String id) {
        // パスの準備
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        Inventory inv;

        inv = Bukkit.createInventory(null,27,guiTitles.get("artsSlotSelector")+" §7id:"+id);
        // 装飾配置
        for (int num = 0; num < 9; num++)
            inv.setItem(num, guiItems.get("grayPane"));
        for (int num = 18; num < 26; num++)
            inv.setItem(num, guiItems.get("grayPane"));

        // ギミックアイテム
        for(int i=9;i<18;i++) {

            // 忍法がセットされているかの確認
            String artID = cfg.getString("art_"+(i-8));
            if(artID != null){

                Matcher m = Pattern.compile("(\\w+)_(\\d+)").matcher(artID);
                String artType;
                int artNumber;
                if(m.find()){
                    artType = m.group(1);
                    artNumber = Integer.parseInt(m.group(2));
                } else {
                    p.sendMessage(prefix+"§cIDの入力形式が違います。§7入力値:"+artID);
                    return;
                }

                inv.setItem(i,makeArtsBook(artType,artNumber));
                continue;
            }

            if(numbers.get("rank_"+cfg.get("rank")+"_arts") >= i-8){
                inv.setItem(i,guiItems.get("artsSelector_emptySlot"));
            } else {
                inv.setItem(i,guiItems.get("artsSelector_lockedSlot"));
            }
        }

        // 戻るアイテム
        inv.setItem(26,guiItems.get("terminal_back"));

        // インベントリを開く
        p.openInventory(inv);
    }

    public static void clickedArtSlotSelector(InventoryClickEvent e) {
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) {
            return;
        }
        // 持ち出し防止
        e.setCancelled(true);
        // ID取得
        String id = getID(e.getView().getTitle());

        // スロット選択
        p.sendMessage("slot:"+e.getSlot()+"bool:"+!e.getCurrentItem().isSimilar(guiItems.get("artsSelector_lockedSlot")));
        if (e.getSlot() > 8 && e.getSlot() < 18 && !e.getCurrentItem().isSimilar(guiItems.get("artsSelector_lockedSlot"))) {
            openArtsSelector(p, id, "hub_slot:" + (e.getSlot() - 8));
        }
        // 前のGUIへ戻る
        else if (e.getCurrentItem().equals(guiItems.get("terminal_back"))){
            openGUI(p,"sheetEditor_id:"+id);
        }
    }

    // 忍法追加GUI
    public static void openArtsSelector(Player p,String id,String type){
        File sheet = getSheetPath(p, id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
        Inventory inv;

        // ハブ
        if(type.startsWith("hub_slot:")){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(type);
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+type);
                return;
            }

            inv = Bukkit.createInventory(null,27,guiTitles.get("artsSelector")+" §7slot:"+artSlot+" §7id:"+id);
            // 装飾配置
            for (int num = 0; num < 9; num++)
                inv.setItem(num, guiItems.get("grayPane"));
            for (int num = 18; num < 26; num++)
                inv.setItem(num, guiItems.get("grayPane"));

            // ギミックアイテム
            inv.setItem(10,guiItems.get("artsSelector_utility_attack"));
            inv.setItem(12,guiItems.get("artsSelector_utility_support"));
            inv.setItem(14,guiItems.get("artsSelector_utility_equipment"));
            inv.setItem(16,guiItems.get("artsSelector_style"));

            // 戻るアイテム
            inv.setItem(26,guiItems.get("terminal_back"));

            // インベントリを開く
            p.openInventory(inv);
        }

        // 汎用忍法 攻撃
        else if(type.startsWith("utility_attack_slot:")){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(type);
            //String artType;
            //if(m.find()){
            //    artType = m.group(1);
            //} else {
            //    p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+type);
            //    return;
            //}
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+type);
                return;
            }
            inv = Bukkit.createInventory(null,54,guiTitles.get("artsSelector")+" §7slot:"+artSlot+" §7id:"+id);
            // 装飾配置
            for (int num = 0; num < 9; num++)
                inv.setItem(num, guiItems.get("grayPane"));

            // ギミックアイテム
            String[][] art = arts.get("utility_attack");
            for(int i = 0,slot = 9;i < art.length; i++,slot++){
                inv.setItem(slot,makeArtsBook("utility_attack",i));
            }

            // 戻るアイテム
            inv.setItem(53,guiItems.get("terminal_back"));

            // インベントリを開く
            p.openInventory(inv);
        }
        // 汎用忍法 サポート
        else if(type.startsWith("utility_support_slot:")){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(type);
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+type);
                return;
            }
            inv = Bukkit.createInventory(null,54,guiTitles.get("artsSelector")+" §7slot:"+artSlot+" §7id:"+id);
            // 装飾配置
            for (int num = 0; num < 9; num++)
                inv.setItem(num, guiItems.get("grayPane"));

            // ギミックアイテム
            String[][] art = arts.get("utility_support");
            for(int i = 0,slot = 9;i < art.length; i++,slot++){
                inv.setItem(slot,makeArtsBook("utility_support",i));
            }

            // 戻るアイテム
            inv.setItem(53,guiItems.get("terminal_back"));

            // インベントリを開く
            p.openInventory(inv);
        }
        // 汎用忍法 装備
        else if(type.startsWith("utility_equipment_slot:")){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(type);
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+type);
                return;
            }
            inv = Bukkit.createInventory(null,54,guiTitles.get("artsSelector")+" §7slot:"+artSlot+" §7id:"+id);
            // 装飾配置
            for (int num = 0; num < 9; num++)
                inv.setItem(num, guiItems.get("grayPane"));

            // ギミックアイテム
            String[][] art = arts.get("utility_equipment");
            for(int i = 0,slot = 9;i < art.length; i++,slot++){
                inv.setItem(slot,makeArtsBook("utility_equipment",i));
            }

            // 戻るアイテム
            inv.setItem(53,guiItems.get("terminal_back"));

            // インベントリを開く
            p.openInventory(inv);
        }

        else if(type.startsWith("style_slot:")){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(type);
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+type);
                return;
            }
            inv = Bukkit.createInventory(null,54,guiTitles.get("artsSelector")+" §7slot:"+artSlot+" §7id:"+id);
            // 装飾配置
            for (int num = 0; num < 9; num++)
                inv.setItem(num, guiItems.get("grayPane"));

            // ギミックアイテム
            p.sendMessage("style_"+cfg.get("style"));
            String[][] art = arts.get("style_"+cfg.get("style"));
            for(int i = 0,slot = 9;i < art.length; i++,slot++){
                inv.setItem(slot,makeArtsBook("style_"+cfg.get("style"),i));
            }

            // 戻るアイテム
            inv.setItem(53,guiItems.get("terminal_back"));

            // インベントリを開く
            p.openInventory(inv);
        }
    }
    public static void clickedArtsSelector(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        // 持ち出し防止
        e.setCancelled(true);
        // ID取得
        String id = getID(e.getView().getTitle());
        // ハブ
        if(e.getCurrentItem().isSimilar(guiItems.get("artsSelector_utility_attack"))){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(e.getView().getTitle());
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+e.getView().getTitle());
                return;
            }
            openArtsSelector(p,id,"utility_attack_slot:"+artSlot);
        } else if(e.getCurrentItem().isSimilar(guiItems.get("artsSelector_utility_support"))){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(e.getView().getTitle());
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+e.getView().getTitle());
                return;
            }
            openArtsSelector(p,id,"utility_support_slot:"+artSlot);
        } else if(e.getCurrentItem().isSimilar(guiItems.get("artsSelector_utility_equipment"))){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(e.getView().getTitle());
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+e.getView().getTitle());
                return;
            }
            openArtsSelector(p,id,"utility_equipment_slot:"+artSlot);
        } else if(e.getCurrentItem().isSimilar(guiItems.get("artsSelector_style"))){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(e.getView().getTitle());
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+e.getView().getTitle());
                return;
            }
            openArtsSelector(p,id,"style_slot:"+artSlot);
        }
        // 忍法
        else if(e.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)){
            Matcher m = Pattern.compile("slot:(\\d+)").matcher(e.getView().getTitle());
            String artSlot;
            if(m.find()){
                artSlot = m.group(1);
            } else {
                p.sendMessage(prefix+"§cスロットの入力形式が違います。§7入力値:"+e.getView().getTitle());
                return;
            }
            List<String> lores = e.getCurrentItem().getItemMeta().getLore();
            if(lores == null){
                return;
            }
            m = Pattern.compile("artID:(.+)").matcher(lores.get(lores.size()-1));
            String artID;
            if(m.find()){
                artID = m.group(1);
            } else {
                p.sendMessage(prefix+"§cIDの入力形式が違います。§7入力値:"+lores.get(lores.size()-1));
                return;
            }

            setSheet(p,id,"art_"+artSlot,artID);
        }
        // 前のGUIへ戻る
        else if (e.getCurrentItem().equals(guiItems.get("terminal_back"))){
            openGUI(p,"artsSelector_id:"+id);
        }
    }
}



