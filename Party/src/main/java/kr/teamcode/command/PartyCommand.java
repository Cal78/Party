package kr.teamcode.command;

import kr.teamcode.Main;
import kr.teamcode.util.Party;
import kr.teamcode.util.PartyPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PartyCommand implements CommandExecutor {
    // 개인 정적 Map
    private static Map<String, Integer> inviteMap = new HashMap<String, Integer>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("플레이어만 입력이 가능합니다.");
        }
        Player p = (Player)sender;
        if(args.length == 0){
            p.sendMessage("/파티 생성");
            p.sendMessage("/파티 인원확인");
            p.sendMessage("/파티 제거");
            p.sendMessage("/파티 나가기");
            p.sendMessage("/파티 초대 [닉네임]");
            p.sendMessage("/파티 추방 [닉네임]");
            return false;
        }

        Party party;
        PartyPlayer pp = PartyPlayer.getPartyPlayer(p);

        switch (args[0]) {
            case "생성":
                if (pp.hasParty()) {
                    p.sendMessage("이미 가입 된 파티가 있습니다!");
                    return false;
                }
                party = new Party(p);
                pp.joinParty(party.getID());
                p.setDisplayName("[파티보유중] " + p.getName());
                p.setPlayerListName("§e[" + p.getName() + "님의 파티] " + p.getName() + "(Master)");
                p.sendMessage("[Party ID] " + Party.ID);
                p.sendMessage("파티를 생성 하였습니다.");
                break;
            case "인원확인":
                if (!pp.hasParty()) {
                    p.sendMessage("가입 된 파티가 없습니다!");
                    return false;
                }
                party = Party.getParty(pp.getPartyId());
                for (Player pP : party.getPlayers())
                    p.sendMessage(pP.getName() + (party.isMaster(pP) ? " §a파티장 (Master)" : " §b파티원"));
                break;
            case "제거":
                if (!pp.hasParty()) {
                    p.sendMessage("가입 된 파티가 없습니다!");
                    return false;
                }
                party = Party.getParty(pp.getPartyId());
                if (!party.isMaster(p)) {
                    p.sendMessage("당신은 권한이 없습니다!");
                    return false;
                }
                p.sendMessage("파티가 삭제 되었습니다.");
                for (Player pP : party.getPlayers()) {
                    PartyPlayer.getPartyPlayer(pP).leaveParty();
                    pP.sendMessage("파티가 삭제 되었습니다!");
                }
                party.removeParty();
                break;
            case "나가기":
                if (!pp.hasParty()) {
                    p.sendMessage("가입 된 파티가 없습니다!");
                    return false;
                }
                party = Party.getParty(pp.getPartyId());
                if (party.isMaster(p)) {
                    p.sendMessage("파티장은 탈퇴할 수 없습니다!");
                    return false;
                }
                party.removeParty();
                pp.leaveParty();
                party.sendMessage(p.getName() + "님이 파티에서 탈퇴 하였습니다.");
                p.sendMessage("파티에서 탈퇴 하였습니다.");
                break;
            case "초대":
                if (args.length < 2) {
                    p.sendMessage("닉네임을 입력 하세요!");
                    return false;
                }
                if(!pp.hasParty()){
                    p.sendMessage("가입 된 파티가 없습니다!");
                    return false;
                }
                party = Party.getParty(pp.getPartyId());
                if (!party.isMaster(p)) {
                    p.sendMessage("당신은 권한이 없습니다!");
                    return false;
                }
                // 플레이어 target 객체
                // getPlayerExact 대소문자를 구분하지 않고 정확한 이름을 가진 플레이어를 가져오는 메소드
                Player target = Bukkit.getPlayerExact(args[1]);
                if(target == null) {
                    p.sendMessage("찾을 수 없는 플레이어...");
                    return false;
                }
                PartyPlayer ppTarget = PartyPlayer.getPartyPlayer(target);
                if(ppTarget.hasParty()){
                    p.sendMessage("이미 가입한 파티가 있는 플레이어입니다.");
                    return false;
                }

                if(inviteMap.containsKey(target.getName())){
                    p.sendMessage("상대방이 다른 초대를 처리 중 입니다..");
                    return false;
                }

                p.sendMessage(target.getName() + "님에게 파티 초대를 성공적으로 전송 하였습니다.");

                // TextComponent 일반 메시지를 만드는데 사용하는 생성자
                TextComponent accept = new TextComponent("§a[파티 수락]");
                accept.setBold(true); // 굵게 표시하는지를 설정하는 메소드 setBold
                // 텍스트를 클릭하면 특정 작업을 수행 시키는 메소드 setClickEvent
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 수락"));
                /*
                ComponentBuilder 메시지를 작성하기 위한 생성자
                텍스트 위로 마우스를 가져가면 생성한 메시지를 기반으로 설명을 표시하는 메소드 setHoverEvent
                 */
                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a클릭시 파티 초대를 수락합니다").create()));
                TextComponent refuse = new TextComponent("§c[파티 거절]");
                refuse.setBold(true);
                refuse.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 거절"));
                refuse.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c클릭시 파티 초대를 거절합니다").create()));
                target.sendMessage(p.getName() + " 님이 파티에 초대 하였습니다. 제한시간 10초");
                // 추상 메소드 spigot(): 스피곳으로 메시지 보내기
                target.spigot().sendMessage(accept);
                target.spigot().sendMessage(refuse);

                // 맵에 초대받은 플레이어의 이름과 파티 ID를 넣는다.
                inviteMap.put(target.getName(), party.getID());
                // 스케줄링 작업을 쉽게 처리할 수 있는 클래스
                new BukkitRunnable() {
                    // 추상 클래스
                    @Override
                    public void run() {
                        // 맵에 해당 키가 있는지 확인하고 그 키의 값이 파티ID와 같을 경우
                        if(inviteMap.containsKey(target.getName()) && inviteMap.get(p.getName()) == party.getID())
                            // 맵에서 항목(key, value) 삭제
                            inviteMap.remove(target.getName());
                    }
                // 지정된 수의 서버 틱 후에 실행되도록 예약 1초당 20tick 즉 20x10 = 10초
                }.runTaskLater(Main.instance, 20 * 10);
                break;
            case "추방":
                if (args.length < 2) {
                    p.sendMessage("닉네임을 입력 하세요!");
                    return false;
                }
                if(!pp.hasParty()){
                    p.sendMessage("가입 된 파티가 없습니다!");
                    return false;
                }
                party = Party.getParty(pp.getPartyId());
                if (!party.isMaster(p)) {
                    p.sendMessage("당신은 권한이 없습니다!");
                    return false;
                }
                Player t = Bukkit.getPlayerExact(args[1]);
                if(t == null) {
                    p.sendMessage("찾을 수 없는 플레이어...");
                    return false;
                }
                ppTarget = PartyPlayer.getPartyPlayer(t);
                if(!ppTarget.hasParty() || ppTarget.getPartyId() != pp.getPartyId()){
                    p.sendMessage("같은 파티원이 아닙니다.");
                    return false;
                }
                if(party.isMaster(t)){
                    p.sendMessage("파티장은 추방할 수 없습니다!");
                    return false;
                }
                party.removePlayer(t);
                t.sendMessage("파티에서 추방 당했습니다.");
                party.sendMessage(t.getName() + "님이 파티에서 추방 당하였습니다.");
                ppTarget.leaveParty();
                break;
            case "수락":
                if(!inviteMap.containsKey(p.getName())){
                    p.sendMessage("초대를 받지 않았습니다.");
                    return false;
                }
                if(pp.hasParty()){
                    p.sendMessage("이미 가입 된 파티가 있습니다.");
                    return false;
                }
                party = Party.getParty(inviteMap.get(p.getName()));
                if(party == null){
                    p.sendMessage("파티를 찾을 수 없습니다!");
                    inviteMap.remove(p.getName());
                    return false;
                }
                party.addPlayer(p);
                party.sendMessage(p.getName() + "님이 파티에 가입 하였습니다.");
                pp.joinParty(party.getID());
                inviteMap.remove(p.getName());
                p.setPlayerListName("§e[" + party.getMaster().getName() + "님의 파티] " + p.getName());
                break;
            case "거절":
                if(!inviteMap.containsKey(p.getName())){
                    p.sendMessage("초대를 받지 않았습니다.");
                    return false;
                }
                party = Party.getParty(inviteMap.get(p.getName()));
                if(party == null){
                    p.sendMessage("파티를 찾을 수 없습니다!");
                    inviteMap.remove(p.getName());
                    return false;
                }
                party.getMaster().sendMessage(p.getName() + "님이 파티 초대를 거절 하였습니다.");
                inviteMap.remove(p.getName());
                p.sendMessage("파티 초대를 거절 하였습니다.");
                break;
        }
        return false;
    }
}
