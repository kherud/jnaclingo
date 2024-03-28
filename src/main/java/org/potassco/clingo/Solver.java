/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.potassco.clingo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.potassco.clingo.configuration.args.Option;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.ModelType;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.symbol.Symbol;

public class Solver {

    public List<AnswerSet> solve(String encoding, String instances, Option... options) {
        return solve(instances + encoding, options);
    }

    public List<AnswerSet> solve(String program, Option... options) {
        Control control = new Control();
        control.getConfiguration().set(options);
        control.add(program);
        control.ground();
        List<AnswerSet> answers = new ArrayList<>();
        try (SolveHandle solveHandle = control.solve(new int[0], null, SolveMode.YIELD)) {
            while (solveHandle.hasNext()) {
                Model model = solveHandle.next();
                Symbol[] symbols = model.getSymbols();
                ModelType type = model.getType();
                long[] cost = model.getCost();
                AnswerSet answer = new AnswerSet(Arrays.asList(symbols), type, cost);
                answers.add(answer);
            }
        }
        control.close();
        return answers;
    }
}
