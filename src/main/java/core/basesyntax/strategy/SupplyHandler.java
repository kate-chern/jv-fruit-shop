package core.basesyntax.strategy;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.model.FruitTransaction;
import java.util.Optional;

public class SupplyHandler implements OperationHandler {
    private final FruitDao fruitDao;

    public SupplyHandler(FruitDao fruitDao) {
        this.fruitDao = fruitDao;
    }

    @Override
    public void handle(FruitTransaction fruitTransaction) {
        Optional<Integer> balance = fruitDao.get(fruitTransaction.getName());
        fruitDao.add(fruitTransaction.getName(), balance.orElse(0) + fruitTransaction.getAmount());
    }
}
