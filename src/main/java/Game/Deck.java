package Game;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    // ArrayList of Cards
    private List<Card> cards = new ArrayList<>();

    public Deck (List<Card>deck){
        if(deck != null){
            for(int i = 0; deck.size() > i && i < 4; i++){
                this.cards.add(deck.get(i));
            }
        }
    }

    public void addCard(Card card){
        if(!cards.contains(card)){ // no duplicates
            cards.add(card);
        }
    }

    public void removeCard(Card card){
        if(card != null){
            cards.remove(card);
        }
    }
    public Card getRandomCard() {
        if(cards != null && cards.size() > 0){
            return cards.get((int)(Math.random() * cards.size()));
        }
        return null;
    }
    public int getSize(){
        if(!isEmpty()){
            return cards.size();
        }
        return 0;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
