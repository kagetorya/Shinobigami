package iobb.kagetorya.shinobigami;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

import static iobb.kagetorya.shinobigami.ShinobigamiTables.setupTables;

public final class Shinobigami extends JavaPlugin {

    private static Shinobigami instance;
    private ShinobigamiCommands command;

    // プラグイン有効化時の処理
    @Override
    public void onEnable() {

        instance = this;
        command = new ShinobigamiCommands();
        getServer().getPluginManager().registerEvents(new ShinobigamiGUI(),this);

        // データフォルダの生成
        if(!getDataFolder().exists()){
            if(getDataFolder().mkdirs()){
                getLogger().info("データフォルダを生成しました。");
            } else {
                getLogger().info("データフォルダの生成に失敗しました。");
            }
        }
        // キャラクターシート管理フォルダの生成
        File sheetFolder = new File(getDataFolder(),"Sheets");
        if(!sheetFolder.exists()){
            if(sheetFolder.mkdirs()){
                getLogger().info("キャラクターシート管理フォルダを生成しました。");
            } else {
                getLogger().info("キャラクターシート管理フォルダの生成に失敗しました。");
            }
        }
        // テーブルのセットアップ
        setupTables();
        // 有効化の報告
        getLogger().info("プラグインが有効化されました。");
    }

    // プラグイン無効化時の処理
    @Override
    public void onDisable() {

        getLogger().info("プラグインが無効化されました。");
    }

    // コマンドの処理委託
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String... args){
        return command.onCommand(s,cmd,label,args);
    }

    // タブ補完の委託
    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String... args){
        return command.onTabComplete(s,cmd,label,args);
    }

    public static Shinobigami getInstance(){
        return instance;
    }
}
