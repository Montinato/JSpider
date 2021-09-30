import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JOptionPane;


public class GameBoard extends JComponent implements MouseListener, MouseMotionListener
{

    public static final int piles = 10;
    public static final int slots = 6; // deck slots, each slot have 10 (piles) cards

    private final int margin = 10;

    private final int cardWidth = 71;
    private final int cardHeight = 96;

    private final Color bgColor = new Color(0, 120, 0);

    private List<Card> allCards; // also it's a junk pile

    private Image cardBack;

    private List<Card>[] pile;
    private List<Card>[] deck;

    private int top; // pointer to top
    private int ptr;

    private int yCutoff;

    private int score;
    private int moves;

    private List<Card> movingPile;

    private int index;

    private int prevMX;
    private int prevMY;

    private String difficulty;

    private Stack<GameState> undoStack;

    private Insets insets;
    
    private int chiamateFixPile = 0;
    
    
    // METODO OK 
    public GameBoard(String difficulty) {
        cardBack = Utility.readImage("images\\back.png");		
        undoStack = new Stack<>();								
        insets = new Insets(0, 0, 0, 0);			 		

        loadImages(difficulty);
        calcYCutoff();
        newGame();

        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    
    // METODO OK 
    public void loadImages(String difficulty) 
    {
    	// System.out.println("Metodo loadImage() ");
    	
        this.difficulty = difficulty;

        int value = -1;

        if (difficulty.equals("Easy")) {
            value = 4;
        }
        else if (difficulty.equals("Medium")) {
            value = 3;
        }
        else {
            value = 1;
        }

        allCards = new ArrayList<>();

        int counter = 0;
        
        //int contaNumero = 1;		//DEBUG
        
        //  QUI CARICO TUTTE LE IMMAGINI DELLE CARTE, sia per le PILE che per i DECK

        while (counter < 8) {
        	
        	// System.out.println("Counter = " + counter);
            
        	for (int suit = value; suit <= 4; suit++) {
                for (int rank = 1; rank <= 13; rank++) {
                	
                    allCards.add(new Card(Utility.readImage("images\\" + rank + "" + suit + ".png"), suit, rank, true));
                    
                    //System.out.println("Carico l'immagine della carta " + rank + " " + suit );
                    
                    // stampaDebug(suit,rank);
                     	
                    //contaNumero++;
                }

                counter++;
            }
            
        }
        
         // System.out.println("Il numero di immagini caricate e': " + contaNumero );
    }
    
    public void stampaDebug(int suit,int rank)
    {
    	if(suit == 1)
        	System.out.print("Fiori : ");
        else if(suit == 2)
        	System.out.print("Quadri : ");
        else if(suit == 3)
        	System.out.print("Cuori : ");
        else if(suit == 4)
        	System.out.print("Picche : ");
        
        
        
        if(rank == 11)
        	System.out.println("J");
        else if(rank == 12)
        	System.out.println("Q");
        else if(rank == 13)
        	System.out.println("K");
        else 
        	System.out.println(rank);
    }
    
    
    // METODO OK, DEVO EFFETTIVAMENTE VEDERE A COSA SERVE 
    public void calcYCutoff() {
        yCutoff = JSpider.height * 3 / 5;
    }
    
    
    /*
     * preconditions:
     * - undoStack is !null
     * - loadImages() is called
     * - calcYCutoff() is called, otherwise fixPile() will be slow
     */

    // CONTROLLO QUESTO 
    
    public void newGame() 
    {
        collectAllCards();
        
       /* System.out.println("Stampa dopo il metodo collectAllCards() ");
        System.out.println("                                              ");
        
        for (Card c : allCards) 
        {
        	stampaDebug(c.suit,c.rank);        
        }
        System.out.println("                                              ");
        System.out.println("Numero di carte presente in allCard (dopo collectAllCards() ) : " + allCards.size());
        System.out.println("                                              ");	*/
        
        // Mischio le carte 
        Collections.shuffle(allCards);
        
        
        /*System.out.println("Stampa dopo il metodo Collection.shuffle() ");
        System.out.println("                                              ");
        
        for (Card c : allCards) 
        {
        	stampaDebug(c.suit,c.rank);        
        }
        
        System.out.println("                                              ");
        System.out.println("Numero di carte presente in allCard (dopo Collection.shuffle() ) : " + allCards.size());
        System.out.println("                                              ");	*/
       
        
        initDeck();
        
        initPiles();
        
        deal();
        
        
        undoStack.clear();
        JSpider.undo.setEnabled(false);

        ptr = -1;
        score = 500;
        moves = 0;
        movingPile = null;
    }
   
    //  METODO OK - Mi serve quando faccio una partita,distribuisce il primo deck di carte e gli altri deck
    private void collectAllCards() 
    {
    	int contDebug = 0;
    	
    	//System.out.println("Metodo collectAllCards() ");
    	
        if (allCards.size() < 104) 
        {
        	// Se le carte presenti in allCards sono meno di 104 entro qui
        	//System.out.println("Entro nell'if del metodo collectAllCard() ");
        	
        	
        	
            for (int i = 0; i < piles; i++)
            {
            	//System.out.println("La pila " + i);
            	
                int cards = pile[i].size();
                
                //System.out.println("Ha dimensione " + cards);

                for (int j = 0; j < cards; j++) {
                    Card card = pile[i].get(j);
                    
                    //System.out.println("Inserisco la carta in posizione " +  i + " " + j );
                    
                    //System.out.println("La carta e' : " );
                    
                    
                    //stampaDebug(card.suit,card.rank);
                    
                   // contDebug++;

                    if (!card.isFaceDown()) {
                    	
                        card.flip();
                    }

                   
                    allCards.add(card);
                    
                   // System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                }
            }
            
            
            //System.out.println("---------------------------------------------------------------------------------------------------------------------");
            //System.out.println("---------------------------------------------------------------------------------------------------------------------");

            for (int i = 0; i <= top; i++) 
            {
            	
            	//System.out.println("TOP vale = " + top);
            	
            
                List<Card> slot = deck[i];
                
                //System.out.println("Scelgo il deck numero " + i );

                int size = slot.size();

                for (int j = 0; j < size; j++) {
                //	System.out.println("Metto a terra la carta " + j);
                    allCards.add(slot.get(j));
                }
            }
            
           // System.out.println("*************************************************************************************************");
            
           // System.out.println("ESCO dall'if del metodo collectAllCard() ");
        }
        
       // System.out.println("Numero di carte =  " + contDebug);
        //System.out.println("Esco dal metodo collectAllCards()");	
        } 
    
    // METODO OK 
    @SuppressWarnings("unchecked")
    private void initDeck() 
    {
    	 System.out.println("Metodo initDeck() ");
    	 
    	 int cont = 0;
    	
        deck = new List[slots];

        top = slots - 1;

        // System.out.println("Ci sono in totale " +  top + " righe ");
        
        int y = JSpider.height - cardHeight - margin - 40 - insets.bottom - 16;
        //System.out.println("y = " + y);

        for (int i = 0; i < slots; i++) 
        {
        	// System.out.println("Posizione riga = " + i);
        	
            deck[i] = new ArrayList<>();

            int x = JSpider.width - cardWidth - margin - insets.left - 10 - (margin + 2) * i;
            //System.out.println("x = " + x );

            for (int j = 0; j < piles; j++) 
            {
            	// System.out.println("Invoco il metodo draw() ");
            	
                Card card = draw();

                card.x = x;
                card.y = y;
                card.width = cardWidth;
                card.height = cardHeight;

                deck[i].add(card);
                cont++;
                
                if(i == slots-1)
                {
                	//System.out.print("La carta scoperta della pila " + j + " -> ");
                	//System.out.print("Aggiungo a riga " + i + " -> ");
                	//stampaDebug(card.suit, card.rank);
                }
            }
            
            // System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        }
        
        //System.out.println("Il numero di carte di initDeck = " + cont);
    }
    
    
    // METODO OK 
    @SuppressWarnings("unchecked")
    private void initPiles() 
    {
    	//System.out.println("                                            ");
    	System.out.println("Metodo initPiles() ");
    	//System.out.println("                                            ");
    	
    	int cont = 0;
    	
        pile = new List[piles];

        for (int i = 0; i < piles; i++) {
            pile[i] = new ArrayList<>();

            int cards = (i < 4) ? 5 : 4;

            for (int j = 0; j < cards; j++) {
                Card card = draw();

                card.width = cardWidth;
                card.height = cardHeight;

                pile[i].add(card);
                cont++;
                
                //System.out.print("Pila " + i + " in posizione " + j + " -> ");
                //stampaDebug(card.suit, card.rank);
                
            }
            //System.out.println("                                                                                          ");

            fixPile(i);
            //chiamateFixPile++;
        }
        
        // System.out.println("Numero di carte initPiles = " + cont);
    }

    
    // METDO OK 
    private Card draw() 
    {
        Card card = allCards.get(0);
        allCards.remove(card);
        return card;
    }
    
    
    // METODO OK - Con questo metodo scopro l'ultima carta di ogni pila e la mostro al player, richiama anche checkForCardToRemove()
    public void deal() 
    {
    	System.out.println("Metodo deal() ");
    	
        for (int i = 0; i < piles; i++) 
        {
        	//System.out.println("Pila : " + i );
        	
            Card card = deck[top].get(i);
            
            pile[i].add(card);
            card.flip();
            
            //System.out.print("Giro la carta della pila " + i + " : ");	// CONTROLLARE IL DEBUG DI QUESTA PARTE 
            
            
            
            //stampaDebug(card.suit, card.rank);
            
            fixPile(i);
            
        }

        deck[top--] = null;
        undoStack.push(new GameState());
        JSpider.undo.setEnabled(false);

        for (int i = 0; i < piles; i++) {
            if (checkForCardsToRemove(i)) {
                score += 100;
            }
        }

        repaint();
        
    }
  
    // METODO OK 
    public void setInsets(Insets insets) {
        this.insets = insets;
        repaint();
    }

    public String getDifficulty() {
        return difficulty;
    }

    // METODO OK 
    public void undo() 
    {
        GameState state = undoStack.pop();

        allCards = state.getAllCards();
        pile = state.getPile();
        top = state.getTop();
        ptr = state.getPtr();

        repaint();

        score--;
        moves++;

        if (!canUndo()) {
            JSpider.undo.setEnabled(false);
        }
    }

    // METODO OK 
    public boolean canUndo() {
        return !undoStack.empty() && !undoStack.peek().isFlagged();
    }

    // METODO OK 
    public void resetGame() 
    {
    	// Se lo stack in cui salvo i GameState e' vuoto return
        if (undoStack.empty()) {
            return;
        }

        // Finché sono presenti GameState in undoStack li rimuovo. Ne rimane solo 1 
        while (undoStack.size() > 1) {
            undoStack.pop();
        }

        // Creo uno state e salvo l'ultima istanza rimasta in undoStack()
        GameState state = undoStack.pop();

        // Richiamo i metodi di utilita' 
        allCards = state.getAllCards();
        pile = state.getPile();
        deck = state.getDeck();
        top = state.getTop();
        ptr = state.getPtr();

        repaint();

        score = 500;
        moves = 0;

        JSpider.undo.setEnabled(false);
    }

    
    // METODO OK 
    public void clearCards() {
        collectAllCards();
        allCards = null;
    }

    
    /*
     * This method is an extension of Graphics::drawString, it handles strings with newlines, thanks to
     * SO!
     */
    private void drawString(Graphics g, String str, int x, int y) {
        for (String line : str.split("\n")) {
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
        }
    }


    public void fixJunk() {
        int y = JSpider.height - cardHeight - margin - 40 - insets.bottom - 16;

        for (int i = 0; i <= ptr; i++) {
            Card card = allCards.get(i);

            card.y = y;
        }
    }

    public void fixDeck() {
        int y = JSpider.height - cardHeight - margin - 40 - insets.bottom - 16;

        for (int i = 0; i <= top; i++) {
            int x = JSpider.width - cardWidth - margin - insets.left - 10 - (margin + 2) * i;

            deck[i].stream().forEach(card -> {
                card.x = x;
                card.y = y;
            });
        }
    }

    public void fixPiles() {
        for (int i = 0; i < piles; i++) {
            fixPile(i);
        }
    }

    // METODO OK 
    private void fixPile(int index) 
    {
    	//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    	
    	//System.out.println("Metodo fixPile() : ");
    	
    	chiamateFixPile++;
        
    	if (pile[index].size() == 0) {
            return;
        }

        int xGap = (JSpider.width - piles * cardWidth) / (piles + 1);
       // System.out.println("xGap :" + xGap);
        
        int topX = xGap + index * cardWidth + index * xGap - insets.left;
        //System.out.println("topX :" + topX);
        
        int yGap = 35;
        //System.out.println("yGap :" + yGap);

        for (int i = 0; i < 6; i++) 
        {
        	//System.out.print("Sono sulla pila " + index );
        	
            int cards = pile[index].size();
         //   System.out.println(" che ha dimensione " + cards + ".");

            Card prevCard = null;

            yGap -= (i < 4) ? 4 : 1;
            
           /* if(i<4)
            	yGap -= 4;
            else
            	yGap-=1;	*/
            
            //System.out.println("yGap :" + yGap);
            

            for (int j = 0; j < cards; j++) {
                Card card = pile[index].get(j);

                // 		???????????????????????????????????
                int topY = (prevCard == null) ? margin : prevCard.y + (prevCard.isFaceDown() ? margin : yGap);
                
               // System.out.println("topY:" + topY);

                card.x = topX;
                card.y = topY;

                prevCard = card;
            }

            int lastY = pile[index].get(pile[index].size() - 1).y;
            
            //System.out.println("lastY : " + lastY);
           // System.out.println("                                                                                     ");

            if (lastY < yCutoff) {
                break;
            }
            
            
           // System.out.println("                                                                                     ");
        }
        
      //  System.out.println("Ho invocato fixPile() " + chiamateFixPile + " volte.");
        
    }

   
    	// METODO OK 
    private boolean showPlayAgainDialog() {
        int[] a = JSpider.tracker.getData(difficulty);

        a[0] = Math.max(a[0], score);
        a[1] = a[1] == 0 ? moves : Math.min(a[1], moves);
        a[2]++;

        int resp = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Game over! You won!",
                JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            newGame();
            return true;
        }

        JSpider.tracker.writeToFile();
        System.exit(0);

        return false; // it will never be reached
    }

    
    // METODO OK  
    private boolean checkForCardsToRemove(int index)
    {
        int suit = -1;
        int rank = 1;

        // Faccio dei controlli sull'ultima carta della pila, che deve essere scoperta
        System.out.println("                                                 ");
        System.out.println("Metodo checkForCardToRemove() ");
        System.out.println("PRIMO FOR ");
               
        for (int i = pile[index].size() - 1; i >= 0 && rank <= 13; i--, rank++)
        {
        	System.out.println("Siamo in posizione " + " " + index + " " +  i + ".");
        	
            Card card = pile[index].get(i);
            
            System.out.print("Considero la carta -> ");
            stampaDebug(card.suit,card.rank);

            if (suit == -1) {
                suit = card.getSuit();
                
            }
            if (suit != card.getSuit()) {
                return false;
            }
            if (card.isFaceDown()) {
                return false;
            }
            if (card.getRank() != rank) {
                return false;
            }
        }

        // Da qui in poi controllo la sequenza della pila 
        if (rank == 14) {
        	
        	System.out.println("                                                                                         ");
        	System.out.println(" SEQUENZA COMPLETATA                                         ");
        	System.out.println("                                                                                         ");
        	
            int y = JSpider.height - cardHeight - margin - 40 - insets.bottom - 16;

            Card prevCard = (ptr == -1) ? null : allCards.get(ptr);
            System.out.print("Considero la carta 2 -> ");
            
            //stampaDebug(prevCard.suit,prevCard.rank);
            
            System.out.println("                                                                       ");
            System.out.println("SECONDO FOR ");
            System.out.println("                                                                       ");
            

            for (int i = pile[index].size() - 1; i >= 0 && --rank >= 1; i--) 
            {
                Card card = pile[index].get(i);
                
                System.out.print("Considero la carta 3 -> ");
                stampaDebug(card.suit,card.rank);

                card.x = (prevCard == null) ? margin : prevCard.x + margin + 2;
                card.y = y;
                
                System.out.print("Giro la carta ");
                
                if(card!=null)
                {
                	stampaDebug(card.suit,card.rank);
                }
                
                card.flip(); // the card must be flipped face down for new game

                
                pile[index].remove(card);
                System.out.print("Rimuovo dalla pila " + index + " la carta ");
                stampaDebug(card.suit,card.rank);
                
                allCards.add(card);
                System.out.println("Aggiungo in allCards la carta ");
                
                ptr++;
            }

            Card last = (pile[index].size() > 0) ? pile[index].get(pile[index].size() - 1) : null;
            
            System.out.print("L'ultima carta della pila " + index + " adesso e' ");
            
            if(last != null)
            {
            	stampaDebug(last.suit,last.rank);
            }
            

            if (last != null && last.isFaceDown()) {
            	System.out.print("Scopro la carta ");
            	stampaDebug(last.suit,last.rank);
                last.flip();
            }

            return true;
        }

        return false;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(bgColor);
        g.fillRect(0, 0, JSpider.width, JSpider.height);

        int xGap = (JSpider.width - piles * cardWidth) / (piles + 1);
        int y = margin;

        for (int i = 0; i < piles; i++) {
            int x = xGap + i * cardWidth + i * xGap - insets.left;

            g.setColor(Color.WHITE);
            g.fillRect(x, y, cardWidth, cardHeight); // a white rectangle border indicates that there's no
                                                     // cards at that pile
            g.setColor(bgColor);
            g.fillRect(x + 1, y + 1, cardWidth - 2, cardHeight - 2);

            pile[i].stream().forEach(card -> {
                g.drawImage(card.isFaceDown() ? cardBack : card.getImage(), card.x, card.y, this);
            });
        }

        for (int i = 0; i <= top; i++) {
            Card card = deck[i].get(0);
            g.drawImage(cardBack, card.x, card.y, this);
        }

        for (int i = 12; i <= ptr; i += 13) {
            Card card = allCards.get(i);
            g.drawImage(card.getImage(), card.x, card.y, this);
        }

        g.setColor(bgColor.darker());
        g.fillRect(JSpider.width / 2 - 100 - insets.left, JSpider.height - cardHeight - margin - insets.bottom - 50,
                200,
                cardHeight);

        StringBuilder sb = new StringBuilder();
        sb.append("Score: ").append(score);
        sb.append("\nMoves: ").append(moves);

        g.setColor(Color.WHITE);
        g.setFont(new Font("consolas", Font.BOLD, 14));
        drawString(g, sb.toString(), JSpider.width / 2 - 50,
                JSpider.height - cardHeight - margin - insets.bottom - 50 + 25);

        if (movingPile != null) {
            movingPile.stream().forEach(card -> {
                g.drawImage(card.getImage(), card.x, card.y, this);
            });
        }
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        int mouseX = e.getX();
        int mouseY = e.getY();

        prevMX = mouseX;
        prevMY = mouseY;

        int index = -1;
        int startFrom = -1;

        outer: for (int i = 0; i < piles; i++) {
            int cards = pile[i].size();

            for (int j = cards - 1; j >= 0; j--) {
                Card card = pile[i].get(j);

                if (card.contains(mouseX, mouseY)) {
                    index = i;
                    startFrom = j;
                    break outer;
                }
            }
        }

        if (index != -1) {
            if (pile[index].get(startFrom).isFaceDown()) {
                return;
            }

            List<Card> touched = pile[index].subList(startFrom, pile[index].size())
                                            .stream()
                                            .collect(Collectors.toCollection(ArrayList::new)); // I know
                                                                                               // it's
                                                                                               // ugly but
                                                                                               // it
                                                                                               // resolves
                                                                                               // ConcurrentModificationException

            if (!JSpider.debug) {
                int suit = -1; // ensures that the selected cards have same suite
                int rank = -1; // ensures that the selected cards follow decreasing numerical order

                int size = pile[index].size() - startFrom;

                for (int i = 0; i < size; i++, rank--) {
                    if (suit == -1) {
                        suit = touched.get(i).getSuit();
                    }
                    if (rank == -1) {
                        rank = touched.get(i).getRank();
                    }
                    if (suit != touched.get(i).getSuit()) {
                        return;
                    }
                    if (rank != touched.get(i).getRank()) {
                        return;
                    }
                }
            }

            undoStack.push(new GameState(allCards, pile, deck, top, ptr));
            pile[index].removeAll(touched);
            movingPile = touched;
            this.index = index;

            repaint();
        }
        else if (top >= 0) {
            Card topCard = deck[top].get(0);
            Card botCard = deck[0].get(0);

            Rectangle rect = new Rectangle(topCard.x, topCard.y, botCard.x + botCard.width - topCard.x,
                    topCard.height);

            if (rect.contains(mouseX, mouseY)) {
                // make sure that there's no empty piles
                for (int i = 0; i < piles; i++) {
                    if (pile[i].size() == 0) {
                        return;
                    }
                }

                deal();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
    	System.out.println("Metodo mouseReleased(): ");
    	
        if (movingPile != null) 
        {
            boolean success = false;
            boolean b = false;

            Card firstCard = movingPile.get(0);
            
            System.out.print("First Card : ");
            
            if(firstCard != null)
            {
            	stampaDebug(firstCard.suit, firstCard.rank);
            }
            
            Card lastCard = movingPile.get(movingPile.size() - 1);
            
            System.out.print("Last Card : ");
            
            if(firstCard != null)
            {
            	stampaDebug(lastCard.suit, lastCard.rank);
            }

            Rectangle dragRegion = new Rectangle(firstCard.x, firstCard.y, firstCard.width,
                    lastCard.y + lastCard.height - firstCard.y);

           
            for (int i = 0; i < piles; i++) {
                if (i == index) {
                    continue;
                }
                
                System.out.println("ENTRO NEL FOR ");

                Card card = (pile[i].size() > 0) ? pile[i].get(pile[i].size() - 1) : null; // last card
                
                System.out.println("Controllo l'ultima carta ");
                
                if(card != null)
                {
                	stampaDebug(card.suit, card.rank);
                }

                if (card != null && card.intersects(dragRegion)
                        && (!JSpider.debug ? (card.getRank() == firstCard.getRank() + 1) : true)) {
                	
                	System.out.println("Confronto le first card e la card corrente ");
                	
                	System.out.print("First Card: ");
                	stampaDebug(firstCard.suit, firstCard.rank);
                	
                	System.out.print("Card corrente: ");
                	stampaDebug(card.suit, card.rank);
                	
                    pile[i].addAll(movingPile);
                    
                    System.out.println("OK, sono ordinate correttamente ! ");

                    System.out.println("Provo a rimuovere le carte ! ");
                    
                    if (checkForCardsToRemove(i)) 
                    {
                        score += 100;
                        System.out.println("Aumento il punteggio che e' di " + score + " ");
                        
                        undoStack.push(new GameState());
                        System.out.println("Creo un nuovo GameState() ");
                        
                        JSpider.undo.setEnabled(false);
                        b = true;

                        if (allCards.size() == 104) {
                            movingPile = null;
                            repaint();
                            
                            System.out.println("Le carte raccolte sono 104.");
                            System.out.println("La partita finisce qui.");

                            if (showPlayAgainDialog()) {
                            	System.out.println("Se vuoi continuare a giocare rispondi ! ");
                                return;
                            }
                        }
                    }

                    fixPile(i);
                    success = true;
                    break;
                }
                else if (card == null && pile[i].size() == 0) {
                    int xGap = (JSpider.width - piles * cardWidth) / (piles + 1);
                    int topX = xGap + i * cardWidth + i * xGap - insets.left;

                    Rectangle rect = new Rectangle(topX, margin, cardWidth, cardHeight); // white
                                                                                         // rectangle
                                                                                         // border
                    
                    System.out.println("Se non ci sono più carte nella pila disegno LO SPAZIO VUOTO");

                    if (rect.intersects(dragRegion)) {
                        pile[i].addAll(movingPile);
                        fixPile(i);
                        success = true;
                        break;
                    }
                }
            }

            if (!success) {
            	
            	System.out.println("Success = FALSE ");
            	
                pile[index].addAll(movingPile);
                undoStack.pop();
            }
            else {
            	System.out.println("Success = TRUE ");
            	
                Card last = (pile[index].size() > 0) ? pile[index].get(pile[index].size() - 1) : null;

                if (last != null && last.isFaceDown()) {
                    last.flip();
                }

                score--;
                moves++;

                if (!b) {
                    JSpider.undo.setEnabled(true);
                }
            }

            fixPile(index);
            movingPile = null;

            repaint();

            if (allCards.size() == 104) {
                showPlayAgainDialog();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (movingPile != null) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            int dx = mouseX - prevMX;
            int dy = mouseY - prevMY;

            movingPile.stream().forEach(c -> c.translate(dx, dy));

            prevMX = mouseX;
            prevMY = mouseY;

            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}