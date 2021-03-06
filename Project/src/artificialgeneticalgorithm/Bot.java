/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artificialgeneticalgorithm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Andrés Movilla
 */
public class Bot {

    /**
     * @return the strategy
     */
    public Strategy getStrategy() {
	return strategy;
    }
    
    public static final String[] COLORS = { 
	"Red", "Green", "Blue", "Orange", "Purple", "Cyan", "Gold", "Black", "White", "Brown"
    };
    public static final String[] BODY_PARTS = {
	"maned", "legged", "scaled", "armed", "bearded", "horned", "clawed", "eyed", "furred", "winged"
    };
    public static final String[] ANIMALS = {
	"reptile", "drake", "big cat", "wyrm", "dragon", "elemental", "gryphon", "giant bug", "big dog", "beast"
    };
    public static final String[] PR_ANIMALS = {
	"tadpole", "toad", "frog", "lizard", "large reptile", "magic reptile", "wyrm", "drake", "dragon", "wyvern"
    };
    
    //<editor-fold defaultstate="collapsed" desc="Fields">
    /**
     * Genetic info.
     */
    private final Genes genes;
    /**
     * Determines hit points of bot.
     */
    private int statHealth;
    /**
     * Determines attack recovery time of bot.
     */
    private int statAttack;
    /**
     * Determines defense recovery time of bot.
     */
    private int statDefense;
    /**
     * Amount to recover for attack and shield per tick.
     */
    private final static float recoverySliver = 0.015f;
    /**
     * Amount of hit points of bot.
     * Goes from 0 to maxHitPoints.
     * If 0 is reached, bot dies.
     */
    private float hitPoints;
    /**
     * Amount of maximum hit points of bot.
     */
    private float maxHitPoints;
    /**
     * Amount of hit points to deduct from opponent bot.
     */
    private float attackValue;
    /**
     * Multiplier to reduce damage received when a full shield is up.
     */
    private float shieldDefense;
    /**
     * Is bot dead flag.
     */
    private boolean dead;
    /**
     * Effectiveness of next attack;
     * Goes from 0.0 to 1.0.
     * Multiplier applied to attackValue.
     */
    private float attackCooldown;
    /**
     * Effectiveness of next shield.
     * Goes from 0.0 to 1.0.
     * Multiplier saved on currentShield when shield is activated.
     */
    private float shieldCooldown;
    /**
     * Effectiveness of current shield.
     */
    private float currentShield;
    /**
     * Amount of shield to remove when hit.
     */
    private final static float shieldPenalty = 0.2f;
    /**
     * Penalty for attacking with the shield up.
     */
    private final static float shieldAttackPenaly = 1.2f;
    
    /**
     * Number of battles the bot has been involved in.
     */
    private int battlesFought;
    /**
     * Number of battles the bot has won.
     */
    private int battlesWon;
    
    /**
     * Complete number of battles the bot has been involved in.
     */
    private int totalBattlesFought;
    /**
     * Complete number of battles the bot has won.
     */
    private int totalBattlesWon;
    
    /**
     * Current bot strategy.
     */
    private Strategy strategy;
    /**
     * Enemy engaged in combat.
     */
    public Bot enemy;
    /**
     * Generation number of bot.
     */
    private final int generation;
    /**
     * Number of bot in generation.
     */
    private final int serialNumber;
    /**
     * Model of bot.
     */
    private final String modelName;
    /**
     * Unique name assigned to bot.
     */
    private final String uniqueName;
    /**
     * Number of generations this bot has been best.
     */
    private int bestGeneration;
    /**
     * Number of generations this bot has survived by being best.
     */
    private int surviveBestGeneration;
    /**
     * Number of generations this bot has survived randomly.
     */
    private int surviveRandomGeneration;
    /**
     * Number of generations this bot has aced.
     */
    private int aceGeneration;
    //</editor-fold>
    
    public Bot(int generation, int serialNumber) {
	this.generation = generation;
	this.serialNumber = serialNumber;
	genes = new Genes();
	init();
//	System.out.println("Bot created!");
	this.modelName = Bot.getModelName(generation, serialNumber);
	this.uniqueName = Bot.createUniqueName(generation, serialNumber);
    }
    
    public Bot(int generation, int serialNumber, Genes genes) {
	this.generation = generation;
	this.serialNumber = serialNumber;
	this.genes = genes;
	init();
//	System.out.println("Bot bred!");
	this.modelName = Bot.getModelName(generation, serialNumber);
	this.uniqueName = Bot.createUniqueName(generation, serialNumber);
    }
    
    public static String getModelName(int iGen, int iNum) {
	String gen = "" + iGen;
	String num = "" + iNum;
	
	while (gen.length() < 3) {
	    gen = "0" + gen;
	}
	
	while (num.length() < 3) {
	    num = "0" + num;
	}
	
	return "G"+gen+"N"+num;
    }
    
    public static String createUniqueName(int iGen, int iNum) {
	if (iGen < 0 || iNum < 0)
	    return "Legend";
	String gen = "" + iGen;
	String num = "" + iNum;
	
	while (gen.length() < 3) {
	    gen = "0" + gen;
	}
	
	while (num.length() < 3) {
	    num = "0" + num;
	}
	
	int[] indexes = { 
	    Integer.parseInt(""+gen.charAt(1)), Integer.parseInt(""+gen.charAt(2)),
	    Integer.parseInt(""+num.charAt(1)), Integer.parseInt(""+num.charAt(2))
	};
	
	String secondColor = ( (indexes[3] == indexes[2])? "" : " "+COLORS[indexes[2]] );
	
	return COLORS[indexes[3]]+"-"+BODY_PARTS[indexes[1]]+secondColor.toLowerCase()+" "+PR_ANIMALS[indexes[0]];
    }
    
    private void init() {
	statHealth = genes.getHealthStat();
	statAttack = genes.getAttackStat();
	statDefense = genes.getDefenseStat();
	
	attackCooldown = 1;
	shieldCooldown = 1;
	currentShield = 0;
	
	bestGeneration = 0;
	surviveBestGeneration = 0;
	aceGeneration = 0;
	surviveRandomGeneration = 0;
	
	dead = false;
	
	strategy = new Strategy(genes);
    
	battlesWon = 0;
	battlesFought = 0;
	
	createStats();
    }
    
    public void survivedBest() {
	this.surviveBestGeneration++;
    }
    
    public void survivedRandomly() {
	this.surviveRandomGeneration++;
    }
    
    public void bestGeneration() {
	this.bestGeneration++;
	if (this.battlesFought == this.battlesWon) {
	    this.aceGeneration++;
	}
    }
    
    private void createStats() {
	hitPoints = 5 + (getStatHealth() * 10);
	maxHitPoints = 5 + (getStatHealth() * 10);
	attackValue = getStatAttack() * 3;
	shieldDefense = getStatDefense() * 2;
    }
    
    public float actionAttack() {
	float damage = attackValue*attackCooldown*(1-(currentShield*shieldAttackPenaly));
	attackCooldown = 0;
	return damage;
    }
    
    public void actionShieldUp() {
	if (currentShield == 0) {
	    currentShield = shieldCooldown;
	}
    }
    
    public void actionShieldDown() {
	currentShield = 0;
    }
    
    public void receiveDamage(float damage) {
	if (!dead) {
	    float shieldReduction = currentShield*shieldDefense;
	    if (shieldReduction < damage) {
		hitPoints -= (damage-shieldReduction);
	    }
	    if (currentShield > 0) {
		shieldCooldown -= shieldPenalty;
	    }
	    dead = (hitPoints <= 0);
	}
    }
    
    public void tick(double count) {
	if (!dead && !enemy.dead) {
	    attackCooldown += recoverySliver;
	    if (currentShield == 0) {
		shieldCooldown += recoverySliver;
	    } else {
		shieldCooldown -= recoverySliver;
	    }
	    if (shieldCooldown < 0) {
		currentShield = 0;
	    }
	    if (shieldCooldown > 1) {
		shieldCooldown = 1;
	    }
	    if (attackCooldown > 1) {
		attackCooldown = 1;
	    }
	    if (count >= getStrategy().getNextActionTime()) {
		doAction(getStrategy().nextAction());
	    }
	}
    }
    
    private void doAction(int actionCode) {
	switch(actionCode) {
	    case Action.ACTION_ATTACK:
		float damage = actionAttack();
//		System.out.println("Attacking for "+damage+"!");
		enemy.receiveDamage(damage);
		break;
	    case Action.ACTION_SHIELD_DOWN:
//		System.out.println("Shield off!");
		actionShieldDown();
		break;
	    case Action.ACTION_SHIELD_UP:
//		System.out.println("Shielding!");
		actionShieldUp();
		break;
	    default:
//		System.out.println("Harden!");
		break;
	}
    }

    public int getWins() {
	return battlesWon;
    }
    
    public Genes getGenes() {
	return genes;
    }
    
    public boolean isAlive() {
	return !dead;
    }
    
    public void resetBattles() {
	this.totalBattlesFought += this.battlesFought;
	this.totalBattlesWon += this.battlesWon;
	
	this.battlesFought = 0;
	this.battlesWon = 0;
    }
    
    public void tallyBattle() {
	battlesFought++;
	if (isAlive()) {
	    battlesWon++;
	}
    }
    
    public void newBattle(Bot enemy) {
	this.enemy = enemy;
	this.attackCooldown = 1;
	this.shieldCooldown = 1;
	this.hitPoints = this.maxHitPoints;
	this.getStrategy().reset();
	this.dead = false;
    }
    
    public void kill() {
	this.dead = true;
    }
    
    public BufferedImage getImage(int alignment) {
	BufferedImage img = new BufferedImage(350,700,BufferedImage.TYPE_INT_ARGB);
	Graphics g = img.getGraphics();
	
	g.setFont(new Font("Arial",Font.BOLD,20));
	g.setColor(Color.BLACK);
	    
	g.fillRect(0, 0, 350, 310);
	g.setColor(Color.white);
	g.fillRect(2, 2, 346, 306);
	g.setColor(Color.BLACK);
	    
	int[] columns = new int[] {20,140,180,300};
	
	g.drawString(uniqueName,10,30);
	g.setFont(new Font("Arial",Font.PLAIN,18));
	g.drawString(getModelName(), columns[0],50);
	    
	g.drawString("Generation:", columns[0], 85);
	g.drawString(""+generation, columns[1], 85);
	g.drawString("Number:", columns[2], 85);
	g.drawString(""+serialNumber, columns[3], 85);

	g.drawString("Aces:", columns[0], 120);
	g.drawString(""+aceGeneration, columns[1], 120);
	g.drawString("Best:", columns[2], 120);
	g.drawString(""+getBestGeneration(), columns[3], 120);
	
	g.drawString("Best survived:", columns[0], 140);
	g.drawString(""+surviveBestGeneration, columns[1], 140);
	
	g.drawString("RNG survived:", columns[2], 140);
	g.drawString(""+surviveRandomGeneration, columns[3], 140);
	
	g.drawString("Total wins:", columns[0], 160);
	g.drawString(""+totalBattlesWon, columns[1], 160);
	g.drawString("Total battles:", columns[2], 160);
	g.drawString(""+totalBattlesFought, columns[3], 160);

	g.drawString("Wins:", columns[0], 190);
	g.drawString(""+getWins(), columns[1], 190);
	g.drawString("Battles:", columns[2], 190);
	g.drawString(""+battlesFought, columns[3], 190);

	String barName = "Victories";
	int barLength = 150;
	int barHeight = 25;
	int barX = columns[0];
	int barY = 200;
	float percent = battlesFought == 0? 0.0f : (float)battlesWon/battlesFought;
	g.fillRect(barX, barY, barLength, barHeight);
	g.setColor(Color.red);
	g.fillRect(barX+2, barY+2, barLength-4, barHeight-4);
	g.setColor(Color.green);
	g.fillRect(barX+2, barY+2, (int)((barLength-4)*percent), barHeight-4);
	g.setColor(Color.BLACK);
	g.drawString(barName, barX+44, barY+18);

	barName = "Battles";
	barX = columns[2];
	percent = (float)battlesFought/(Handler.numberBots-1);
	g.fillRect(barX, barY, barLength, barHeight);
	g.setColor(Color.red);
	g.fillRect(barX+2, barY+2, barLength-4, barHeight-4);
	g.setColor(Color.green);
	g.fillRect(barX+2, barY+2, (int)((barLength-4)*percent), barHeight-4);
	g.setColor(Color.BLACK);
	g.drawString(barName, barX+46, barY+18);

	barName = "Health";
	barLength = 310;
	barY = 250;
	barX = columns[0];
	percent = (float)hitPoints/maxHitPoints;
	g.fillRect(barX, barY, barLength, barHeight);
	g.setColor(Color.red);
	g.fillRect(barX+2, barY+2, barLength-4, barHeight-4);
	g.setColor(Color.green);
	g.fillRect(barX+2, barY+2, (int)((barLength-4)*percent), barHeight-4);
	g.setColor(Color.BLACK);
	g.drawString(barName, barX+135, barY+18);

	barName = "Attack";
	barLength = 150;
	barY = 280;
	barX = columns[0];
	percent = (float)attackCooldown;
	g.fillRect(barX, barY, barLength, barHeight);
	g.setColor(Color.white);
	g.fillRect(barX+2, barY+2, barLength-4, barHeight-4);
	g.setColor(Color.red);
	g.fillRect(barX+2, barY+2, (int)((barLength-4)*percent), barHeight-4);
	g.setColor(Color.BLACK);
	g.drawString(barName, barX+48, barY+18);

	barName = "Shield";
	barX = columns[2];
	percent = (float)shieldCooldown;
	g.fillRect(barX, barY, barLength, barHeight);
	g.setColor(Color.white);
	g.fillRect(barX+2, barY+2, barLength-4, barHeight-4);
	g.setColor(Color.blue);
	g.fillRect(barX+2, barY+2, (int)((barLength-4)*percent), barHeight-4);
	g.setColor(Color.BLACK);
	g.drawString(barName, barX+48, barY+18);

		
	if (alignment == 0) {
	    g.drawImage(getStrategy().getImage(),0,310,null);
	    g.drawImage(genes.getImage(alignment),170,310,null);
	    
	    g.fillRect(170, 440, 180, 170);
	    g.setColor(Color.white);
	    g.fillRect(172, 442, 176, 166);
	    g.setColor(Color.BLACK);
	
	    g.drawString("Health:", 190, 510);
	    g.drawString("Attack:", 190, 540);
	    g.drawString("Defense:", 190, 570);

	    g.drawString(""+getStatHealth(), 310, 510);
	    g.drawString(""+getStatAttack(), 310, 540);
	    g.drawString(""+getStatDefense(), 310, 570);
	    
	    g.setFont(new Font("Arial",Font.BOLD,25));
	    g.drawString("Stats",230,480);
	} else {
	    
	    g.drawImage(getStrategy().getImage(),180,310,null);
	    g.drawImage(genes.getImage(alignment),0,310,null);
	    
	    g.fillRect(0, 440, 180, 170);
	    g.setColor(Color.white);
	    g.fillRect(2, 442, 176, 166);
	    g.setColor(Color.BLACK);
	
	    g.drawString("Health:", 20, 510);
	    g.drawString("Attack:", 20, 540);
	    g.drawString("Defense:", 20, 570);

	    g.drawString(""+getStatHealth(), 120, 510);
	    g.drawString(""+getStatAttack(), 120, 540);
	    g.drawString(""+getStatDefense(), 120, 570);
	    
	    g.setFont(new Font("Arial",Font.BOLD,25));
	    g.drawString("Stats",40,480);
	}
	
	return img;
    }
    
    public BufferedImage getBriefImage() {
	BufferedImage img = new BufferedImage(350,80,BufferedImage.TYPE_INT_ARGB);
	Graphics g = img.getGraphics();
	
	g.setFont(new Font("Arial",Font.BOLD,20));
	g.setColor(Color.BLACK);
	    
	g.fillRect(0, 0, 350, 80);
	g.setColor(Color.white);
	g.fillRect(2, 2, 346, 76);
	g.setColor(Color.BLACK);
	
	g.drawString(uniqueName,10,30);
	
	g.setFont(new Font("Arial",Font.PLAIN,18));
	g.drawString(getModelName(), 30, 60);
	
	String barName = "Victories";
	int barLength = 150;
	int barHeight = 25;
	int barX = 180;
	int barY = 40;
	float percent = battlesFought == 0? 0.0f : (float)battlesWon/battlesFought;
	g.fillRect(barX, barY, barLength, barHeight);
	g.setColor(Color.red);
	g.fillRect(barX+2, barY+2, barLength-4, barHeight-4);
	g.setColor(Color.green);
	g.fillRect(barX+2, barY+2, (int)((barLength-4)*percent), barHeight-4);
	g.setColor(Color.BLACK);
	g.drawString(barName, barX+44, barY+18);
	
	return img;
    }

    public BufferedImage getLastImage() {
	
	BufferedImage img = new BufferedImage(350,700,BufferedImage.TYPE_INT_ARGB);
	Graphics g = img.getGraphics();
	
	g.setFont(new Font("Arial",Font.BOLD,20));
	g.setColor(Color.BLACK);
	    
	g.fillRect(0, 0, 350, 310);
	g.setColor(Color.white);
	g.fillRect(2, 2, 346, 306);
	g.setColor(Color.BLACK);
	    
	int[] columns = new int[] {20,140,180,300};
	
	g.drawString(uniqueName,10,30);
	g.setFont(new Font("Arial",Font.PLAIN,18));
	g.drawString(getModelName(), columns[0],50);
	    
	g.drawString("Generation:", columns[0], 90);
	g.drawString(""+generation, columns[1], 90);
	g.drawString("Number:", columns[2], 90);
	g.drawString(""+serialNumber, columns[3], 90);

	g.drawString("Aces:", columns[0], 130);
	g.drawString(""+aceGeneration, columns[1], 130);
	g.drawString("Best:", columns[2], 130);
	g.drawString(""+getBestGeneration(), columns[3], 130);
	g.drawString("Survived:", columns[0], 150);
	g.drawString(""+surviveBestGeneration, columns[1], 150);

	g.drawString("Wins:", columns[0], 190);
	g.drawString(""+totalBattlesWon, columns[1], 190);
	g.drawString("Battles:", columns[2], 190);
	g.drawString(""+totalBattlesFought, columns[3], 190);

	String barName = "Victories";
	int barLength = 310;
	int barHeight = 25;
	int barX = columns[0];
	int barY = 200;
	float percent = totalBattlesFought == 0? 0.0f : (float)totalBattlesWon/totalBattlesFought;
	g.fillRect(barX, barY, barLength, barHeight);
	g.setColor(Color.red);
	g.fillRect(barX+2, barY+2, barLength-4, barHeight-4);
	g.setColor(Color.green);
	g.fillRect(barX+2, barY+2, (int)((barLength-4)*percent), barHeight-4);
	g.setColor(Color.BLACK);
	g.drawString(barName, barX+44, barY+18);

	g.drawImage(getStrategy().getImage(),180,310,null);
	g.drawImage(genes.getImage(0),0,310,null);

	g.fillRect(0, 440, 180, 170);
	g.setColor(Color.white);
	g.fillRect(2, 442, 176, 166);
	g.setColor(Color.BLACK);

	g.drawString("Health:", 20, 510);
	g.drawString("Attack:", 20, 540);
	g.drawString("Defense:", 20, 570);

	g.drawString(""+getStatHealth(), 120, 510);
	g.drawString(""+getStatAttack(), 120, 540);
	g.drawString(""+getStatDefense(), 120, 570);

	g.setFont(new Font("Arial",Font.BOLD,25));
	g.drawString("Stats",40,480);
	
	return img;
    }
    
    /**
     * @return the modelName
     */
    public String getModelName() {
	return modelName;
    }

    /**
     * @return the best
     */
    public int getBestGeneration() {
	return bestGeneration;
    }

    /**
     * @return the statHealth
     */
    public int getStatHealth() {
	return statHealth;
    }

    /**
     * @return the statAttack
     */
    public int getStatAttack() {
	return statAttack;
    }

    /**
     * @return the statDefense
     */
    public int getStatDefense() {
	return statDefense;
    }
}
