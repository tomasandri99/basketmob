package is.hi.basketmob.notification;

import is.hi.basketmob.entity.Game;

public interface GameUpdateObserver {
    void onGameFinal(Game game);
}
