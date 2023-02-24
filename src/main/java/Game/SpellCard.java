package Game;

public class SpellCard extends Card{

    public SpellCard(String id, String name, float damage, ElementType elementType){
        super(id, name, damage, elementType);
    }

    public boolean instaKill(final Card other){

        if(other instanceof MonsterCard){
            MonsterCard otherMonsterCard = (MonsterCard) other;
            if(this.getElementType() == ElementType.Water && otherMonsterCard.getMonsterType() == MonsterType.Knight){
                return true;
            }
            return false;
        }
        return false;
    }
}
