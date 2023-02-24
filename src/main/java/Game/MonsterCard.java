package Game;

public class MonsterCard extends Card{

    private MonsterType monsterType;

    public MonsterCard(String id, String name, float damage,MonsterType monsterType, ElementType elementType){
        super(id, name, damage, elementType);
        this.monsterType = monsterType;
   }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(MonsterType monsterType) {
        this.monsterType = monsterType;
    }

    public boolean instaKill(final Card other){
        if(other instanceof MonsterCard){
            MonsterCard otherMonsterCard = (MonsterCard) other;
            if(this.getMonsterType() == MonsterType.Dragon && otherMonsterCard.getMonsterType() == MonsterType.Goblin){
                return true;
            }
            if(this.getMonsterType() == MonsterType.Wizard && otherMonsterCard.getMonsterType() == MonsterType.Ork){
                return true;
            }
            if(this.getMonsterType() == MonsterType.FireElf && otherMonsterCard.getMonsterType() == MonsterType.Dragon){
                return true;
            }
        }

        if(other instanceof SpellCard){
            if(this.getMonsterType() == MonsterType.Kraken){
                return true;
            }
        }
        return false;
    }
}
