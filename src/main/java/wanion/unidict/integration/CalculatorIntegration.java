package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import sonar.calculator.mod.common.recipes.machines.AlgorithmSeparatorRecipes;
import sonar.calculator.mod.common.recipes.machines.StoneSeparatorRecipes;
import wanion.unidict.UniDict;

import java.util.Map;

final class CalculatorIntegration extends AbstractIntegrationThread
{
    CalculatorIntegration()
    {
        super("Calculator");
    }

    @Override
    public String call()
    {
        try {
            fixStoneSeparatorRecipes();
            fixAlgorithmSeparatorRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "so much calculations were done.";
    }

    private void fixStoneSeparatorRecipes()
    {
        Map<Object[], Object[]> recipes = StoneSeparatorRecipes.instance().getRecipes();
        recipes.values().forEach(resourceHandler::setMainItemStacks);
    }

    private void fixAlgorithmSeparatorRecipes()
    {
        Map<Object[], Object[]> recipes = AlgorithmSeparatorRecipes.instance().getRecipes();
        recipes.values().forEach(resourceHandler::setMainItemStacks);
    }
}