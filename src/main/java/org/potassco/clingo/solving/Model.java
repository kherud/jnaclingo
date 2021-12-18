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
 
package org.potassco.clingo.solving;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;
import org.potassco.clingo.control.ShowType;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.symbol.Symbol;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Provides access to a model during a solve call and provides a `SolveContext` object to influence the running search.
 *
 * The string representation of a model object is similar to the output of models by clingo using the default output.
 *
 * `Model` objects cannot be constructed from Java. Instead they are obained
 *  during solving (see `Control.solve`). Furthermore, the lifetime of a model
 *  object is limited to the scope of the callback it was passed to or until
 *  the search for the next model is started. They must not be stored for later
 *  use.
 */
public class Model {

    private final Pointer model;

    public Model(Pointer model) {
        this.model = model;
    }

    /**
     * Efficiently check if an atom is contained in the model.
     * The atom must be represented using a function symbol.
     *
     * @param atom The atom to lookup.
     * @return Whether the given atom is contained in the model.
     */
    public boolean contains(Symbol atom) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_contains(model, atom.getLong(), byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Extend a model with the given symbols.
     *
     * This only has an effect if there is an underlying clingo application, which will print the added symbols.
     *
     * @param symbols The symbols to add to the model.
     */
    public void extend(Collection<Symbol> symbols) {
        long[] symbolLongs = symbols.stream().mapToLong(Symbol::getLong).toArray();
        Clingo.check(Clingo.INSTANCE.clingo_model_extend(model, symbolLongs, new NativeSize(symbolLongs.length)));
    }

    /**
     * Check if the given program literal is true.
     *
     * @param symbolicAtom The given program literal.
     * @return Whether the given program literal is true.
     */
    public boolean isTrue(SymbolicAtom symbolicAtom) {
        return false;
        // TODO: symbolic atoms needed here
//        ByteByReference byteByReference = new ByteByReference();
//        Clingo.INSTANCE.clingo_model_is_true(model, symbolicAtom);
    }

    /**
     * Return the list of atoms, terms, or CSP assignments in the model.
     *
     * Atoms are represented using functions (`Symbol` objects), and CSP
     * assignments are represented using functions with name `"$"` where the
     * first argument is the name of the CSP variable and the second its
     * value.
     *
     * @return All projected symbols.
     */
    public Symbol[] getSymbols() {
        return getSymbols(ShowType.shown());
    }

    /**
     * Return the list of atoms, terms, or CSP assignments in the model.
     *
     * Atoms are represented using functions (`Symbol` objects), and CSP
     * assignments are represented using functions with name `"$"` where the
     * first argument is the name of the CSP variable and the second its
     * value.
     *
     * @return The selected symbols.
     */
    public Symbol[] getSymbols(ShowType showType) {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_symbols_size(model, showType.getBitset(), nativeSizeByReference));
        int modelSize = (int) nativeSizeByReference.getValue();
        long[] modelSymbols = new long[modelSize];

        Clingo.check(Clingo.INSTANCE.clingo_model_symbols(
                model,
                showType.getBitset(),
                modelSymbols,
                new NativeSize(modelSize))
        );

        Symbol[] symbols = new Symbol[modelSize];

        for (int i = 0; i < modelSize; i++) {
            symbols[i] = Symbol.fromLong(modelSymbols[i]);
        }

        return symbols;
    }

    @Override
    public String toString() {
        Symbol[] symbols = getSymbols(ShowType.shown());
        return Arrays.stream(symbols).map(Symbol::toString).collect(Collectors.joining(" "));
    }

    /**
     * @return Object that allows for controlling the running search.
     */
    public SolveControl getContext() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_context(model, pointerByReference));
        return new SolveControl(pointerByReference.getValue());
    }

    /**
     * Return the list of integer cost values of the model.
     * The return values correspond to clasp's cost output.
     *
     * @return array of integer costs
     */
    public int[] getCost() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_cost_size(model, nativeSizeByReference));
        int costSize = (int) nativeSizeByReference.getValue();
        Clingo.check(Clingo.INSTANCE.clingo_model_cost(model, pointerByReference, new NativeSize(costSize)));

        return costSize > 0 ? new int[0] : pointerByReference.getValue().getIntArray(0, costSize);
    }

    /**
     * @return The running number of the model.
     */
    public long getNumber() {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_number(model, longByReference));
        return longByReference.getValue();
    }

    /**
     * @return Whether the optimality of the model has been proven.
     */
    public boolean getOptimalityProven() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_optimality_proven(model, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @return The id of the thread which found the model.
     */
    public int getThreadId() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_thread_id(model, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return The type of the model.
     */
    public ModelType getType() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_model_type(model, intByReference));
        return ModelType.fromValue(intByReference.getValue());
    }

    public Pointer getPointer() {
        return model;
    }

}
