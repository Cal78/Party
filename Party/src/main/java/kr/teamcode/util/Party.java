package kr.teamcode.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Party {
    // 개인 정적 객체 Map
    private static Map<Integer, Party> party = new HashMap<Integer, Party>();
    // 개인 정적 변수 createID
    private static int createID = 0; // 2번째 생성에선 CreateID = 1
    // 공개 정적 변수 id
    public static int ID;
    // 개인 플레이어 객체 master
    private Player master;
    // 개인 객체 Set
    private Set<Player> players;

    // 생성자
    public Party(Player master){
        // this 인스턴스 자신을 가리키는 키워드
        this.master = master;
        // HashSet 선언
        players = new HashSet<>();
        // Set에 master를 추가
        this.players.add(master);
        // 증감 연산자, createID 값을 먼저 사용한 후 1 증가
        this.ID = createID++;
        // 파티맵에 파티 id와 생성된 파티를 넣는다.
        party.put(ID, this);
    }

    // 파티를 불러오는 메소드
    public static Party getParty(int id){
        return party.containsKey(id) ? party.get(id) : null;
    }

    // id값을 불러오는 메소드
    public int getID(){
        return ID;
    }

    // 마스터(파티장)를 불러오는 메소드
    public Player getMaster(){
        return master;
    }

    // 마스터를 변경하는 메소드
    public void changeMaster(){
        for(Player p : players){
            master = p;
            return;
        }
    }

    // 플레이어를 파티에 추가하는 메소드
    public void addPlayer(Player p){
        players.add(p);
    }

    // 플레이어를 파티에서 제거 시키는 메소드
    public void removePlayer(Player p){
        players.remove(p);
    }

    // 플레이어가 마스터인지 확인하는 메소드
    public boolean isMaster(Player p){
        return master.equals(p);
    }

    // 파티를 제거하는 메소드
    public void removeParty(){
        party.remove(ID);
    }

    // 파티에 있는 모든 플레이어를 불러오는 메소드
    public Set<Player> getPlayers(){
        return players;
    }

    // 모든 파티원에게 메시지를 보내는 메소드
    public void sendMessage(String msg){
        for(Player p : players){
            p.sendMessage(msg);
        }
    }

}
