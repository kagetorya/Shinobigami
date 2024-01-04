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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static iobb.kagetorya.shinobigami.ShinobigamiSkillManager.openSkillMenu;
import static iobb.kagetorya.shinobigami.ShinobigamiUtils.*;

public class ShinobigamiGUI implements Listener {
    private static final Map<String, ItemStack> playerInfoItems = new HashMap<>();
    static {
        playerInfoItems.put("sheet",makeItemStack(new ItemStack(Material.PAPER),"§7- §eキャラクターシート §7-","§fクリックすると所持キャラクターの","§f一覧を開き、それぞれの項目を参照できます。"));

        playerInfoItems.put("hasuba",makeItemStack((new ItemStack(Material.CRAFTING_TABLE)),"§d流派§7: §f斜歯忍軍 §7-ハスバ-","§f流儀§7: §f他の流派の「奥義の内容」を集める。","§e得意分野: 器術 §8/ §c仇敵: 鞍馬神流"," ","§f雑賀衆の流れを汲み、忍器の研究/開発を得意とする流派。","§f世の中すべての忍法を解析し、誰でも使える忍器へ","§f落とし込むことに並々ならぬ労力を費やす。","§f流派の目標は斜歯を全統一流派にすることである。","§8style id: hasuba"));
        playerInfoItems.put("kurama",makeItemStack((new ItemStack(Material.BOW)),"§d流派§7: §f鞍馬神流 §7-クラマ-","§f流儀§7: §fシノビガミの復活を阻止する。","§e得意分野: 体術 §8/ §c仇敵: 隠忍の血統"," ","§f京八流を源流に持つ戦士達の流派。","§f武術の達人で構成されており、その戦闘技術は","§fシノビガミ顕現にまつわる六神器を他勢力から","§f守り、封印するために用いられる。","§8style id: kurama"));
        playerInfoItems.put("hagure",makeItemStack((new ItemStack(Material.ENDER_PEARL)),"§d流派§7: §fハグレモノ §7-ハグレ-","§f流儀§7: §f誰にも縛られず、自分の意志で戦う。","§e得意分野: 忍術 §8/ §c仇敵: 斜歯忍軍"," ","§f厳密には流派ではないが無視できない勢力。","§fフリーの忍者や抜け忍、小さな里や血盟で結ばれた者たち。","§f緩やかに協力することもあるが特定の目的は持たない。","§8style id: hagure"));
        playerInfoItems.put("hirasaka",makeItemStack((new ItemStack(Material.GOLD_INGOT)),"§d流派§7: §f比良坂機関 §7-ヒラサカ-","§f流儀§7: §f日本の国益を守る。","§e得意分野: 詐術 §8/ §c仇敵: 私立御斎学園"," ","§f比良坂流古神道を背景に持つ日本の諜報機関。","§f政府との繋がりが強く、国防/国益を最優先に動く。","§f政治力や経済力を用いた陰謀や工作を得意とし暗躍する。","§8style id: hirasaka"));
        playerInfoItems.put("otogi",makeItemStack((new ItemStack(Material.WRITABLE_BOOK)),"§d流派§7: §f私立御斎学園 §7-オトギ-","§f流儀§7: §f誰かの秘密を探す。","§e得意分野: 戦術 §8/ §c仇敵: ハグレモノ"," ","§f表向きには小中高一貫の進学校。","§f実際には、異能を持つが制御しきれない少年少女への","§f忍者教育を施す忍者養成機関である。","§f国内外の財閥や研究/諜報機関の支援を受ける留学者も多い。","§8style id: otogi"));
        playerInfoItems.put("oni",makeItemStack((new ItemStack(Material.REDSTONE_TORCH)),"§d流派§7: §f隠忍ノ血統 §7-オニ-","§f流儀§7: §fシノビガミ復活に関する情報を入手する。","§e得意分野: 妖術 §8/ §c仇敵: 比良坂機関"," ","§f古代日本において鬼や土蜘蛛と呼ばれた者の末裔。","§f鬼や人狼のような人外が多く「荒夜§7-ナイト・ゴーント§f」とも呼ばれる。","§f権力者達から虐げられた歴史を持ち、国家権力に強い憎悪を抱く。","§8style id: oni"));

        playerInfoItems.put("grass",makeItemStack(new ItemStack(Material.GRASS),"§a階級§7: §f草忍","§d忍法: 1§8/§b特技: 4§8/§e奥義: なし","§8rank id: grass"));
        playerInfoItems.put("low",makeItemStack(new ItemStack(Material.LEATHER_BOOTS),"§a階級§7: §f下忍","§d忍法: 2§8/§b特技: 5§8/§e奥義: なし","§8rank id: low"));
        playerInfoItems.put("lowtop",makeItemStack(new ItemStack(Material.LEATHER_HELMET),"§a階級§7: §f下忍頭","§d忍法: 3§8/§b特技: 5§8/§e奥義: なし","§8rank id: lowtop"));
        playerInfoItems.put("mid",makeItemStack(new ItemStack(Material.IRON_BOOTS),"§a階級§7: §f中忍","§d忍法: 4§8/§b特技: 6§8/§e奥義: 1","§8rank id: mid"));
        playerInfoItems.put("midtop",makeItemStack(new ItemStack(Material.IRON_HELMET),"§a階級§7: §f中忍頭","§d忍法: 5§8/§b特技: 6§8/§e奥義: 1","§7※功績点を10消費","§8rank id: midtop"));
        playerInfoItems.put("high",makeItemStack(new ItemStack(Material.DIAMOND_BOOTS),"§a階級§7: §f上忍","§d忍法: 6§8/§b特技: 7§8/§e奥義: 2","§7※功績点を20消費","§8rank id: high"));
        playerInfoItems.put("hightop",makeItemStack(new ItemStack(Material.DIAMOND_HELMET),"§a階級§7: §f上忍頭","§d忍法: 7§8/§b特技: 7§8/§e奥義: 2","§7※功績点を50消費","§8rank id: hightop"));
        playerInfoItems.put("head",makeItemStack(new ItemStack(Material.WITHER_SKELETON_SKULL),"§a階級§7: §f頭領","§d忍法: 8§8/§b特技: 8§8/§e奥義: 3","§7※功績点を100消費","§8rank id: head"));
    }

    public static void openGUI(Player p,String guiName) {

        // プレイヤー用GUI [最初に開かれる階層]
        if (guiName.equalsIgnoreCase("playerInfo")) {
            openPlayerInfoGUI(p);
        }
        // キャラクターシート管理GUI
        else if(guiName.equalsIgnoreCase("sheets")) {
            openCharacterManagerGUI(p);
        }
        // 指定キャラクターの編集
        else if(guiName.startsWith("charaediter id:")){
            openCharacterEditer(p,getID(guiName));
        }
        // 流派選択GUI
        else if(guiName.startsWith("styleselector id:")){
            openStyleSelector(p,getID(guiName));
        }
        // 階級選択GUI
        else if(guiName.startsWith("rankselector id:")){
            openRankSelector(p,getID(guiName));
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
        if(e.getView().getTitle().equalsIgnoreCase("§8- §dプレイヤー情報 §8-")){
            clickedPlayerInfoGUI(e);
        } else if(e.getView().getTitle().equalsIgnoreCase("§8- §eキャラクターシート一覧 §8-")){
            clickedCharacterManagerGUI(e);
        } else if (e.getView().getTitle().startsWith("§8- §6キャラクターシート §8- §7ID:")) {
            clickedCharacterEditer(e);
        } else if(e.getView().getTitle().startsWith("§8- §5流派選択 §8- §7id:")){
            clickedStyleSelector(e);
        } else if(e.getView().getTitle().startsWith("§8- §3階級選択 §8- §7id:")) {
            clickedRankSelector(e);
        }
    }

    // プレイヤー用GUI
    public static void openPlayerInfoGUI(Player p){
        Inventory inv = Bukkit.createInventory(null,27,"§8- §dプレイヤー情報 §8-");

        // GUI装飾配置
        ItemStack deco = makeItemStack(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)," "," ");
        for (int num = 0; num < 9; num++) {
            inv.setItem(num,deco);
            inv.setItem(num+18,deco);
        }
        deco = makeItemStack(new ItemStack(Material.IRON_BARS)," ", " ");
        inv.setItem(9,deco);
        inv.setItem(17,deco);

        // ギミックアイテム
        inv.setItem(11,playerInfoItems.get("sheet"));

        p.openInventory(inv);
    }
    public static void clickedPlayerInfoGUI(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }

        e.setCancelled(true);

        if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7- §eキャラクターシート §7-")){
            openGUI(p,"sheets");
        }
    }

    // キャラクターシート管理GUI
    public static void openCharacterManagerGUI(Player p){
        Inventory inv = Bukkit.createInventory(null, 27, "§8- §eキャラクターシート一覧 §8-");

        // GUI装飾配置
        ItemStack deco = makeItemStack(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), " ", " ");
        for (int num = 0; num < 9; num++)
            inv.setItem(num, deco);

        if (getCharacterIDs(p).isEmpty()) {
            inv.setItem(13, makeItemStack(new ItemStack(Material.BARRIER), "§cキャラクターシートが存在しません。", "§f以下のコマンドで作成できます。", "§f[/shinobi charactersheet make (好きなID)]", " ", "§7※IDはファイル管理用なのでキャラ名とは別の物です。"));
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
        p.openInventory(inv);
    }
    public static void clickedCharacterManagerGUI(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        e.setCancelled(true);

        if(e.getCurrentItem().getType().equals(Material.PAPER)){
            String id = e.getCurrentItem().getItemMeta().getDisplayName().replace("§b","");
            openGUI(p,"charaediter id:"+id);
        }
    }

    // 指定キャラクターの編集
    public static void openCharacterEditer(Player p,String id){
        Inventory inv = Bukkit.createInventory(null,27,"§8- §6キャラクターシート §8- §7ID:"+id);
        File sheet = getSheetPath(p,id);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);

        // GUI装飾配置
        ItemStack deco = makeItemStack(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), " ", " ");
        for (int num = 0; num < 9; num++)
            inv.setItem(num, deco);

        // 名前/背景情報
        String[] back;
        String str = cfg.getString("back");
        if(str == null){
            back = new String[]{"§c背景情報は未設定です。", "§f以下のコマンドで名前を設定できます。", "§f[/shinobi charactersheet back " + id + " (好きな内容)]"};
        } else {
            back = str.split("\\|");
        }
        inv.setItem(10,makeItemStack(new ItemStack(Material.NAME_TAG),(String) cfg.get("name"),back));

        // 流派情報
        if(cfg.get("style") == null){
            inv.setItem(12,makeItemStack(new ItemStack(Material.BARRIER),"§d流派§7: §c未設定","§fクリックすることで設定出来ます。"));
        } else {
            inv.setItem(12, playerInfoItems.get(cfg.getString("style")));
        }

        // 特技情報
        inv.setItem(14,makeItemStack(new ItemStack(Material.REDSTONE),"§9特技","§fクリックで設定できます。"));

        p.openInventory(inv);
    }
    public static void clickedCharacterEditer(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        e.setCancelled(true);

        String id = e.getView().getTitle().replace("§8- §6キャラクターシート §8- §7ID:","");
        if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§d流派§7:")){
            openGUI(p,"styleselector id:"+id);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§9特技")){
            openSkillMenu(p,id);
        }
    }

    // 流派選択GUI
    public static void openStyleSelector(Player p,String id){
        Inventory inv = Bukkit.createInventory(null,27,"§8- §5流派選択 §8- §7id:"+id);

        ItemStack deco = makeItemStack(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " ");
        for (int num = 0; num < 9; num++)
            inv.setItem(num, deco);

        inv.setItem(11,playerInfoItems.get("hasuba"));
        inv.setItem(13,playerInfoItems.get("kurama"));
        inv.setItem(15,playerInfoItems.get("hagure"));
        inv.setItem(20,playerInfoItems.get("hirasaka"));
        inv.setItem(22,playerInfoItems.get("otogi"));
        inv.setItem(24,playerInfoItems.get("oni"));

        p.openInventory(inv);
    }
    public static void clickedStyleSelector(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        e.setCancelled(true);

        String id = e.getView().getTitle().replace("§8- §5流派選択 §8- §7id:","");

        if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§d流派§7:")){
            List<String> lores = e.getCurrentItem().getItemMeta().getLore();
            if(lores == null){
                return;
            }
            String lastLore = lores.get(lores.size()-1);
            String cont = lastLore.replace("§8style id: ","");

            setCharacterInfo(p,id,"style",cont);
            openGUI(p,"charaediter id:"+id);
        }
    }

    // 階級選択GUI
    public static void openRankSelector(Player p,String id){
        Inventory inv = Bukkit.createInventory(null,27,"§8- §3階級選択 §8- §7id:"+id);

        ItemStack deco = makeItemStack(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), " ", " ");
        for (int num = 0; num < 9; num++)
            inv.setItem(num, deco);

        inv.setItem(10,playerInfoItems.get("grass"));
        inv.setItem(12,playerInfoItems.get("low"));
        inv.setItem(14,playerInfoItems.get("lowtop"));
        inv.setItem(16,playerInfoItems.get("mid"));
        inv.setItem(19,playerInfoItems.get("midtop"));
        inv.setItem(21,playerInfoItems.get("high"));
        inv.setItem(23,playerInfoItems.get("hightop"));
        inv.setItem(25,playerInfoItems.get("head"));

        p.openInventory(inv);
    }
    public static void clickedRankSelector(InventoryClickEvent e){
        // 例外処理
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }
        e.setCancelled(true);

        String id = e.getView().getTitle().replace("§8- §3階級選択 §8- §7id:","");

        if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a階級§7:")){
            List<String> lores = e.getCurrentItem().getItemMeta().getLore();
            if(lores == null){
                return;
            }
            String lastLore = lores.get(lores.size()-1);
            String cont = lastLore.replace("§8rank id: ","");

            setCharacterInfo(p,id,"rank",cont);
            openGUI(p,"charaediter id:"+id);
        }
    }
}



