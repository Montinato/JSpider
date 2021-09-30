import java.awt.Image;
import java.awt.Rectangle;

public class Card extends Rectangle {

    private Image cardImage;
    
    // suit e rank devono essere private, li ho resi public per fare del debug

    public int suit;			// 	Seme
    public int rank;			//  Valore Carta 

    private boolean isFaceDown;

    public Card(Image cardImage, int suit, int rank, boolean isFaceDown) 
    {
        super();
        this.cardImage = cardImage;
        this.suit = suit;
        this.rank = rank;
        this.isFaceDown = isFaceDown;
    }

    public void flip() {
        isFaceDown = !isFaceDown;
    }

    public Image getImage() {
        return cardImage;
    }

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    public boolean isFaceDown() {
        return isFaceDown;
    }

    @Override
    public Object clone() {
        Card copy = new Card(cardImage, suit, rank, isFaceDown);

        copy.x = x;
        copy.y = y;
        copy.width = width;
        copy.height = height;

        return copy;
    }

}
