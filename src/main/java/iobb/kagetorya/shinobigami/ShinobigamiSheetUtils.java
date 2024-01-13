package iobb.kagetorya.shinobigami;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static iobb.kagetorya.shinobigami.ShinobigamiTables.prefix;

public class ShinobigamiSheetUtils {

    private static final Logger logger = Logger.getLogger(ShinobigamiUtils.class.getName());
    private static final File sheetFolder = new File(Shinobigami.getInstance().getDataFolder(),"Sheets");

    // プレイヤーのシートパス取得
    public static File getSheetPath(Player p, String id){

        // UUIDをファイル名にしたフォルダの準備
        File playerFolder = new File(sheetFolder, p.getUniqueId().toString());

        // フォルダが存在するかのチェック
        if(!playerFolder.exists()){
            // シート管理フォルダの生成処理
            if(playerFolder.mkdirs()){
                p.sendMessage(prefix+"§fプレイヤー別シート管理フォルダを生成しました。");
            } else {
                p.sendMessage("§5§lS§dhinobi §7》§f§cプレイヤー別シート管理フォルダの生成に失敗しました。");
            }
        }
        // パスの返還
        return new File(playerFolder,id+".yml");
    }

    // プレイヤーのシートファイルコンフィグの取得
    public static FileConfiguration getConfig(Player p,String id) {
        // シートパスの取得
        File sheet = getSheetPath(p, id);
        // ファイルコンフィグの返還
        return YamlConfiguration.loadConfiguration(sheet);
    }

    public static void setSheet(Player p,String id,String tag,String cont){
        
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
            logger.log(Level.SEVERE,"couldn't save file");
        }

        // 処理報告とエフェクト
        //p.sendMessage("§5§lS§dhinobi §7》データが設定されました。§7id: "+id);
        //p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
    }

}
