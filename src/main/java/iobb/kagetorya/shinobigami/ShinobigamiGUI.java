package iobb.kagetorya.shinobigami;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import java.util.Map;

import static iobb.kagetorya.shinobigami.ShinobigamiUtils.*;

public class ShinobigamiGUI implements Listener {
    private static final Map<String, ItemStack> playerInfoItems = new HashMap<>();
    static {
        playerInfoItems.put("sheet",makeItemStack(new ItemStack(Material.PAPER),"§7- §eキャラクターシート §7-","§fクリックすると所持キャラクターの","§f一覧を開き、それぞれの項目を参照できます。"));

        playerInfoItems.put("hasuba",makeItemStack((new ItemStack(Material.BOW)),"§d流派§f: 斜歯忍軍 §7-ハスバ-","§f流儀§7: 他の流派の「奥義の内容」を集める。","§e得意分野: 器術 §8/ §c仇敵: 鞍馬神流"," ","§f雑賀衆の流れを汲み、忍器の研究/開発を得意とする流派。","§f世の中すべての忍法を解析し、誰でも使える忍器へ","§f落とし込むことに並々ならぬ労力を費やす。","§f流派の目標は斜歯を全統一流派にすることである。"));
        playerInfoItems.put("kurama",makeItemStack((new ItemStack(Material.IRON_SWORD)),"§d流派§7: 鞍馬神流 §7-クラマ-","§f流儀§7: シノビガミの復活を阻止する。","§e得意分野: 体術 §8/ §c仇敵: 隠忍の血統"," ","§f京八流を源流に持つ戦士達の流派。","§f武術の達人で構成されており、その戦闘技術は","§fシノビガミ顕現にまつわる六神器を他勢力から","§f守り、封印するために用いられる。"));
        playerInfoItems.put("hagure",makeItemStack((new ItemStack(Material.ENDER_PEARL)),"§d流派§7: ハグレモノ §7-ハグレ-","§f流儀§7: 誰にも縛られず、自分の意志で戦う。","§e得意分野: 忍術 §8/ §c仇敵: 斜歯忍軍"," ","§f厳密には流派ではないが無視できない勢力。","フリーの忍者や抜け忍、小さな里や血盟で結ばれた者たち。","緩やかに協力することもあるが特定の目的は持たない。"));
        playerInfoItems.put("hirasaka",makeItemStack((new ItemStack(Material.GOLD_INGOT)),"§d流派§7: 比良坂機関 §7-ヒラサカ-","§f流儀§7: 日本の国益を守る。","§e得意分野: 詐術 §8/ §c仇敵: 私立御斎学園"," ","§f比良坂流古神道を背景に持つ日本の諜報機関。","§f政府との繋がりが強く、国防/国益を最優先に動く。","§f政治力や経済力を用いた陰謀や工作を得意とし暗躍する。"));
        playerInfoItems.put("otogi",makeItemStack((new ItemStack(Material.IRON_SWORD)),"§d流派§7: 私立御斎学園 §7-オトギ-","§f流儀§7: 誰かの秘密を探す。","§e得意分野: 戦術 §8/ §c仇敵: ハグレモノ"," ","§f表向きには小中高一貫の進学校。","§f実際には、異能を持つが制御しきれない少年少女への","忍者教育を施す忍者養成機関である。","§f国内外の財閥や研究/諜報機関の支援を受ける留学者も多い。"));
        playerInfoItems.put("oni",makeItemStack((new ItemStack(Material.IRON_SWORD)),"§d流派§7: 隠忍ノ血統 §7-オニ-","§f流儀§7: シノビガミ復活に関する情報を入手する。","§e得意分野: 妖術 §8/ §c仇敵: 比良坂機関"," ","§f古代日本において鬼や土蜘蛛と呼ばれた者の末裔。","§f鬼や人狼のような人外が多く「荒夜§7-ナイト・ゴーント§f」とも呼ばれる。","§f権力者達から虐げられた歴史を持ち、国家権力に強い憎悪を抱く。"));

    }

    public static void openGUI(Player p,String guiName) {

        if (guiName.equalsIgnoreCase("playerInfo")) {
            Inventory inv = Bukkit.createInventory(null,27,"§8- §dプレイヤー情報　§8-");

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

        else if(guiName.equalsIgnoreCase("sheets")) {
            Inventory inv = Bukkit.createInventory(null, 27, "§8- §eキャラクターシート §8-");

            // GUI装飾配置
            ItemStack deco = makeItemStack(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), " ", " ");
            for (int num = 0; num < 9; num++)
                inv.setItem(num, deco);

            if (getCharacterIDs(p) == null) {
                inv.setItem(13, makeItemStack(new ItemStack(Material.BARRIER), "§cキャラクターシートが存在しません。", "§f以下のコマンドで作成できます。", "§f[/shinobi charactersheet make (好きなID)]", " ", "§7※IDはファイル管理用なのでキャラ名とは別の物です。"));
                return;
            }

            // キャラクターシートの一覧表示
            int slot = 9;
            for(String id : getCharacterIDs(p)){
                File sheet = new File(new File(Shinobigami.getInstance().getDataFolder(),"Sheets"),id+".yml");
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
                if(cfg.get("name") == null){
                    inv.setItem(slot,makeItemStack(new ItemStack(Material.PAPER),"§b"+id,"§f名前: §c未設定"," ","以下のコマンドで名前を設定できます。","§f[/shinobi charactersheet name "+id+" (好きな名前)]"));
                } else {
                    inv.setItem(slot,makeItemStack(new ItemStack(Material.PAPER),"§b"+id,"§f名前: "+cfg.get("name").toString()," ","クリックで詳細設定へ移動できます。"));
                }
                slot ++;
            }
        }

        else if(guiName.startsWith("id:")){
            String id = guiName.replace("id:","");
            Inventory inv = Bukkit.createInventory(null,27,"§9§lID: §8"+id);
            File sheet = new File(new File(Shinobigami.getInstance().getDataFolder(),"Sheets"),id+".yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(sheet);
            cfg.set("editing",id);

            // GUI装飾配置
            ItemStack deco = makeItemStack(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), " ", " ");
            for (int num = 0; num < 9; num++)
                inv.setItem(num, deco);

            // 名前/背景情報
            String[] back;
            if(cfg.get("back") == null){
                back = new String[]{"§c背景情報は未設定です。", "以下のコマンドで名前を設定できます。", "§f[/shinobi charactersheet back " + id + " (好きな内容)]"};
            } else {
                back = new String[]{cfg.getString("back", "|")};
            }
            inv.setItem(10,makeItemStack(new ItemStack(Material.NAME_TAG),cfg.get("name").toString(),back));

            // 流派情報
            if(cfg.get("style") == null){
                inv.setItem(12,makeItemStack(new ItemStack(Material.BARRIER),"§d流派§f: §c未設定","§fクリックすることで設定出来ます。"));
            } else {
                inv.setItem(12, playerInfoItems.get(cfg.get("style")));
            }



        }
        else if(guiName.startsWith("style")){
            Inventory inv = Bukkit.createInventory(null,27,"§8- §dStyle Selector §8-");

            ItemStack deco = makeItemStack(new ItemStack(Material.RED_STAINED_GLASS_PANE), " ", " ");
            for (int num = 0; num < 9; num++)
                inv.setItem(num, deco);

            inv.setItem(11,playerInfoItems.get("hasuba"));
            inv.setItem(13,playerInfoItems.get("kurama"));
            inv.setItem(15,playerInfoItems.get("hagure"));
            inv.setItem(20,playerInfoItems.get("hirasaka"));
            inv.setItem(22,playerInfoItems.get("otogi"));
            inv.setItem(24,playerInfoItems.get("oni"));
        }
        // 例外処理
        else {
            p.sendMessage("§cError: そのGUIは存在しません。");
        }
    }

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta() == null){
            return;
        }

        if(e.getView().getTitle().equalsIgnoreCase("§8- §dプレイヤー情報　§8-")){
            e.setCancelled(true);

            if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7- §eキャラクターシート §7-")){
                openGUI(p,"sheets");
            }
        } else if(e.getView().getTitle().equalsIgnoreCase("§8- §eキャラクターシート §8-")){
            e.setCancelled(true);

            if(e.getCurrentItem().getType().equals(Material.PAPER)){
                String id = e.getCurrentItem().getItemMeta().getDisplayName().replace("§b","");
                openGUI(p,"id:"+id);
            }
        }
    }
}

