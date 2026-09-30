package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.actions.*;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientPlayer;
import ca.hexanome04.splendorgame.model.action.ActionResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Tests for doing actions in a game.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ActionsTests {

    @DisplayName("Ensure players cannot buy a card with insufficient tokens in their inventory.")
    @Test
    void testPlayerBuyCard_InsufficientTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 3);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // should be no more tier 1 cards available to be purchased
        assertThat(p1.getDevCards().isEmpty()).isTrue();
        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure players can buy a card when sufficient tokens provided.")
    @Test
    void testPlayerBuyCard_SufficientTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);

        p1.addTokens(tokensToUse);

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using normal and gold tokens.")
    @Test
    void testPlayerBuyCard_UseGold() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Blue, 1);
        tokensToAdd.put(TokenType.Gold, 1);
        tokensToAdd.put(TokenType.Red, 2);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 1);
        tokensToUse.put(TokenType.Gold, 1);
        tokensToUse.put(TokenType.Red, 2);

        Card expected = game.getCardFromId("02");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses.")
    @Test
    void testPlayerBuyCard_UseBonus() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Red, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 1);

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndGold() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Gold, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Gold, 1);

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(game.getCardFromId(expected.getId())).isNull();
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and double gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndDoubleGoldTokensOnly() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 2);
        p1.addBonus(TokenType.Gold, 2);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        Card expected = game.getCardFromId("01");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(game.getCardFromId(expected.getId())).isNull();
        assertThat(expected).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }
}
