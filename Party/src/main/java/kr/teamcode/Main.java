package kr.teamcode;

import kr.teamcode.command.PartyCommand;
import kr.teamcode.event.PlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    // Main 클래스를 불러오기 위한 instance
    public static Main instance;

    @Override
    public void onEnable() {
        // this 인스턴스 자신을 가리키는 키워드
        instance = this;
        getCommand("파티").setExecutor(new PartyCommand());
        getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.setDisplayName(all.getName());
            all.setPlayerListName(all.getName());
        }
        System.out.println("[Party] 플러그인 활성화");
    }
}
