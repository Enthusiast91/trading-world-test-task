package com.google.enthusiast91.app.elements;

import java.util.*;
import java.util.stream.Collectors;

class Budget {

    /**
     * Временное хранилище.
     * Используется для хранения прибыли до наступления следующего месяца.
     */
    private final HashMap<Integer, Coin> temporaryStorage = new HashMap<>();

    /**
     * Основное хранилище.
     * Деньги которые идут на расходы.
     */
    private final HashMap<Integer, Coin> mainStorage = new HashMap<>();

    private int moneyForMonth = 0;

    int getSizeMainStorage() {
        return mainStorage.size();
    }

    int getMoneyForMonth() {
        return moneyForMonth;
    }

    int getMoneyMainStorage() {
        return mainStorage.values()
                .stream()
                .mapToInt(Coin::getValue)
                .sum();
    }

    /**
     * <li>Перед началом нового месяца деньги из временного хранилища перевозят в казну.</li>
     */
    void calculateMoneyForMonth() {
        for (Coin coin : temporaryStorage.values()) {
            addCoinToMainStorage(coin);
        }
        temporaryStorage.clear();
        moneyForMonth = getMoneyMainStorage() / 2;
    }

    /**
     * Создание набора монет для оплаты:
     * <li>- Несколько раз перебираем монеты казны, доступные для расходов;</li>
     * <li>- Добавляем по 1-ой монете всех доступных валют, пока не наберётся неоходимое количество.</li>
     *
     * @return Набор монет для оплаты расходов.
     */
    HashMap<Integer, Coin> subCoins(int valueOfExpenses) {
        moneyForMonth -= valueOfExpenses;
        HashMap<Integer, Coin> coinMap = new HashMap<>();

        List<Integer> countryNumberSortedList = mainStorage.values().stream()
                .sorted((Coin c1, Coin c2) -> Integer.compare(c2.getValue(), c1.getValue()))
                .map(Coin::getNumCountry)
                .collect(Collectors.toList());

        for (Integer countryNumber : countryNumberSortedList) {
            int valueSubAtIter = (valueOfExpenses > mainStorage.size()) ? (valueOfExpenses / mainStorage.size()) : 1;
            Coin coin = mainStorage.get(countryNumber).subValue(valueSubAtIter);
            coinMap.put(countryNumber, coin);
            valueOfExpenses -= coin.getValue();

            if (coin.getValue() == 0) {
                mainStorage.remove(countryNumber);
            }
            if(valueOfExpenses <= 0) {
                return coinMap;
            }
        }
        return coinMap;
    }

    /**
     * Для того что-бы страна сразу же не воспользовалась деньгами, которые ей пришли в этом месяце
     * прибыль помещается во временное хранилище, до наступления следующего месяца.
     */
    void addCoins(HashMap<Integer, Coin> coins) {
        for (Coin coin : coins.values()) {
            addCoin(coin, temporaryStorage);
        }
    }

    void addCoinToMainStorage(Coin coin) {
        if (coin.getValue() == 0) {
            return;
        }
        addCoin(coin, mainStorage);
    }

    private void addCoin(Coin coin, HashMap<Integer, Coin> storage) {
        if (coin.getValue() == 0) {
            return;
        }
        if (storage.containsKey(coin.getNumCountry())) {
            storage.get(coin.getNumCountry()).addValue(coin.getValue());
        } else {
            storage.put(coin.getNumCountry(), coin);
        }
    }
}