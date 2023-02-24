package Game;

public abstract class Card {
    private String id;
    private String name;
    private ElementType elementType;
    private float damage;

    private int bonusPoints = 0;

    public Card() {

    }

    public Card(String id, String name, float damage, ElementType elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public void setName() {
        this.name = name;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public void levelUp() {
        this.bonusPoints++;
        if (this.bonusPoints > 3) {
            this.damage *= 1.25;
            this.bonusPoints = 0;
        }
    }

    public float calculateDamage(final Card other) {
        float result = this.getDamage();
        if (this instanceof MonsterCard && other instanceof MonsterCard) { // The element type does not effect pure monster fights.
            return result;
        }
        if (this.elementType == other.getElementType()) {
            return result;
        }
        if (this.elementType == ElementType.Water && other.elementType == ElementType.Fire || this.elementType == ElementType.Fire && other.elementType == ElementType.Normal || this.elementType == ElementType.Normal && other.elementType == ElementType.Water) {
            return result * 2;
        }
        if (this.elementType == ElementType.Fire && other.elementType == ElementType.Water || this.elementType == ElementType.Normal && other.elementType == ElementType.Fire || this.elementType == ElementType.Water && other.elementType == ElementType.Normal) {
            return result / 2;
        }
        return result;
    }

    public abstract boolean instaKill(final Card other);


}
