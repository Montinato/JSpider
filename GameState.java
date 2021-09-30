import java.util.ArrayList;
import java.util.List;

public class GameState 
{

    private List<Card> allCards;

    private List<Card>[] pile;
    private List<Card>[] deck;

    private int top;
    private int ptr;

    private boolean flag;

    public GameState() {
        flag = true;
    }

    @SuppressWarnings("unchecked")
    public GameState(List<Card> allCards, List<Card>[] pile, List<Card>[] deck, int top, int ptr) {
        this.allCards = new ArrayList<>();

        for (Card card : allCards) {
            this.allCards.add((Card) card.clone());
        }

        this.pile = new List[JSpider.board.piles];

        for (int i = 0; i < JSpider.board.piles; i++) {
            this.pile[i] = new ArrayList<>();

            for (Card card : pile[i]) {
                this.pile[i].add((Card) card.clone());
            }
        }

        this.deck = new List[JSpider.board.slots];

        for (int i = 0; i < JSpider.board.slots; i++) {
            if (deck[i] == null) {
                this.deck[i] = null;
            }
            else {
                this.deck[i] = new ArrayList<>();

                for (Card card : deck[i]) {
                    this.deck[i].add((Card) card.clone());
                }
            }
        }

        this.top = top;
        this.ptr = ptr;

        flag = false;
    }

    public List<Card> getAllCards() {
        return allCards;
    }

    public List<Card>[] getPile() {
        return pile;
    }

    public List<Card>[] getDeck() {
        return deck;
    }

    public int getTop() {
        return top;
    }

    public int getPtr() {
        return ptr;
    }

    public boolean isFlagged() {
        return flag;
    }
}
