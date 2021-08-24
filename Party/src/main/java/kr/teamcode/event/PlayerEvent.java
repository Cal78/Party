package kr.teamcode.event;

import kr.teamcode.util.Party;
import kr.teamcode.util.PartyPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        // 플레이어를 기본 파티에 가입
        PartyPlayer.registerParty(e.getPlayer());
    }

     @EventHandler
     public void onEntityDamage(EntityDamageByEntityEvent e) {
         // 공격받은 엔티티 객체
         Entity victim = e.getEntity();
         // 공격하는 엔티티 객체
         Entity attacker = e.getDamager();
         // 공격하는 엔티티가 플레이어이고, 공격받은 플레이어도 플레이어일 경우
         if (attacker instanceof Player && victim instanceof Player) {
             // 공격하는 엔티티와 공격받은 엔티티의 파티 ID값이 -1일 경우
             if (PartyPlayer.getPartyPlayer((Player) attacker).getPartyId() == -1 && PartyPlayer.getPartyPlayer((Player) victim).getPartyId() == -1) {
                 // 이벤트 취소 상태를 false (때려진다)
                 e.setCancelled(false);
                 // 공격하는 엔티티와 공격받은 엔티티의 파티 ID값이 일치할 경우
             } else if (PartyPlayer.getPartyPlayer((Player) attacker).getPartyId() == PartyPlayer.getPartyPlayer((Player) victim).getPartyId()) {
                 // 데미지를 0으로 설정
                 e.setDamage(0.0D);
                 // 이벤트 취소 상태를 true (때려지지 않는다)
                 e.setCancelled(true);
                 // 공격하는 엔티티에게 메시지 전송
                 attacker.sendMessage("같은 파티원은 공격할 수 없습니다!");
             }
         }
     }



    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
         // 플레이어 객체
         Player p = e.getPlayer();
         // 파티플레이어 객체
         PartyPlayer pp = PartyPlayer.getPartyPlayer(p);
         // 플레이어가 파티에 있을경우
         if(pp.hasParty()){
             // 파티 객체
             Party party = Party.getParty(pp.getPartyId());
             // 파티에 인원수 크기값이 1일 경우
             if(party.getPlayers().size() == 1){
                 // 파티를 제거
                 party.removeParty();
                 // 파티 나가기
                 pp.leaveParty();
             // 인원수가 1이 아닐 경우
             } else {
                 // 파티에서 플레이어를 제거
                 party.removePlayer(p);
                 // 플레이어가 파티 마스터일 경우
                 if(party.isMaster(p)) {
                     // 파티장을 변경
                     party.changeMaster();
                     // 파티원에게 변경된 파티장을 알리는 메시지 전송
                     party.sendMessage(p.getName() + "님이 파티를 떠나 파티장이 " + party.getMaster().getName() + "님으로 변경 되었습니다.");
                 } else {
                     // 파티장이 아닐 경우 파티원이 떠난 메시지 전송
                     party.sendMessage(p.getName() + " 님이 접속을 종료하여, 파티를 떠났습니다.");
                 }
                 // 파티 나가기
                 pp.leaveParty();
             }
         }
     }
}
