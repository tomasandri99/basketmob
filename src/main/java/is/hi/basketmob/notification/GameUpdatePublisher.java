package is.hi.basketmob.notification;

import is.hi.basketmob.entity.Game;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GameUpdatePublisher {

    private final List<GameUpdateObserver> observers = new CopyOnWriteArrayList<>();

    public void register(GameUpdateObserver observer) {
        observers.add(observer);
    }

    public void notifyFinal(Game game) {
        observers.forEach(observer -> observer.onGameFinal(game));
    }
}
