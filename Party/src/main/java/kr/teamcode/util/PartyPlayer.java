package kr.teamcode.util;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class PartyPlayer {
    // 개인 정적 객체 Map
    private static Map<String, PartyPlayer> partyPlayer = new HashMap<String, PartyPlayer>();
    // 개인 변수 partyID
    private int partyID;

    // 생성자
    public PartyPlayer(Player p){
        // 변수 partyID에 -1이라는 값을 대입
        partyID = -1;
        // 파티 플레이어 맵에 플레이어 이름과 파티정보를 넣는다.
        partyPlayer.put(p.getName(), this);
    }

    // 파티 기본 가입 메소드
    public static void registerParty(Player p){
        if(!partyPlayer.containsKey(p.getName()))
            new PartyPlayer(p);
    }

    // 파티 플레이어를 불러오는 메소드
    public static PartyPlayer getPartyPlayer(Player p){
        // 삼항 연산자 (디스코드 확인)
        return partyPlayer.containsKey(p.getName()) ? partyPlayer.get(p.getName()) : new PartyPlayer(p);
    }

    // 파티 ID를 불러오는 메소드
    public int getPartyId(){
        return partyID;
    }

    // 플레이어가 파티에 가입했는지 확인하는 메소드
    public boolean hasParty(){
        return partyID != -1;
    }

    // 파티 입장 메소드
    public void joinParty(int partyID){
        // 플레이어 partyID를 들어간 partyID 값으로 대입
        this.partyID = partyID;
    }

    // 파티 나가기 메소드
    public void leaveParty(){
        // partyID를 기본값 -1로 대입
        partyID = -1;
    }
}
