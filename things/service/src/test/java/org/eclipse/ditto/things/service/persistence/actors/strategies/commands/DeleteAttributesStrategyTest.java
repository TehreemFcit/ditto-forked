/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.things.service.persistence.actors.strategies.commands;

import static org.eclipse.ditto.things.model.TestConstants.Thing.THING_V2;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeException;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.modify.DeleteAttributes;
import org.eclipse.ditto.things.model.signals.commands.modify.DeleteAttributesResponse;
import org.eclipse.ditto.things.model.signals.events.AttributesDeleted;
import org.junit.Before;
import org.junit.Test;

import com.typesafe.config.ConfigFactory;

/**
 * Unit test for {@link DeleteAttributesStrategy}.
 */
public final class DeleteAttributesStrategyTest extends AbstractCommandStrategyTest {

    private DeleteAttributesStrategy underTest;

    @Before
    public void setUp() {
        final ActorSystem system = ActorSystem.create("test", ConfigFactory.load("test"));
        underTest = new DeleteAttributesStrategy(system);
    }

    @Test
    public void successfullyDeleteAllAttributesFromThing() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final DeleteAttributes command = DeleteAttributes.of(context.getState(), provideHeaders(context));

        assertStagedModificationResult(underTest, THING_V2, command,
                AttributesDeleted.class,
                DeleteAttributesResponse.of(context.getState(), command.getDittoHeaders()));
    }

    @Test
    public void deleteAttributesFromThingWithoutAttributes() {
        final CommandStrategy.Context<ThingId> context = getDefaultContext();
        final DeleteAttributes command = DeleteAttributes.of(context.getState(), provideHeaders(context));
        final DittoRuntimeException expectedException =
                ExceptionFactory.attributesNotFound(context.getState(), command.getDittoHeaders());

        assertErrorResult(underTest, THING_V2.removeAttributes(), command, expectedException);
    }

}
