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

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.entity.metadata.Metadata;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.headers.WithDittoHeaders;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultFactory;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.things.model.ThingId;
import org.eclipse.ditto.things.model.signals.commands.ThingCommandSizeValidator;
import org.eclipse.ditto.things.model.signals.commands.modify.ModifyAttribute;
import org.eclipse.ditto.things.model.signals.commands.modify.ModifyAttributeResponse;
import org.eclipse.ditto.things.model.signals.events.AttributeCreated;
import org.eclipse.ditto.things.model.signals.events.AttributeModified;
import org.eclipse.ditto.things.model.signals.events.ThingEvent;

/**
 * This strategy handles the {@link ModifyAttribute} command.
 */
@Immutable
final class ModifyAttributeStrategy extends AbstractThingModifyCommandStrategy<ModifyAttribute> {

    /**
     * Constructs a new {@code ModifyAttributeStrategy} object.
     *
     * @param actorSystem the actor system to use for loading the WoT extension.
     */
    ModifyAttributeStrategy(final ActorSystem actorSystem) {
        super(ModifyAttribute.class, actorSystem);
    }

    @Override
    protected Result<ThingEvent<?>> doApply(final Context<ThingId> context,
            @Nullable final Thing thing,
            final long nextRevision,
            final ModifyAttribute command,
            @Nullable final Metadata metadata) {

        final Thing nonNullThing = getEntityOrThrow(thing);

        final JsonObject thingWithoutAttributeJsonObject =
                nonNullThing.removeAttribute(command.getAttributePointer()).toJson();
        final JsonValue attributeJsonValue = command.getAttributeValue();

        ThingCommandSizeValidator.getInstance().ensureValidSize(
                () -> {
                    final long lengthWithOutAttribute = thingWithoutAttributeJsonObject.getUpperBoundForStringSize();
                    final long attributeLength = attributeJsonValue.getUpperBoundForStringSize()
                            + command.getAttributePointer().length() + 5L;
                    return lengthWithOutAttribute + attributeLength;
                },
                () -> {
                    final long lengthWithOutAttribute = thingWithoutAttributeJsonObject.toString().length();
                    final long attributeLength = attributeJsonValue.toString().length()
                            + command.getAttributePointer().length() + 5L;
                    return lengthWithOutAttribute + attributeLength;
                },
                command::getDittoHeaders);

        return nonNullThing.getAttributes()
                .filter(attributes -> attributes.contains(command.getAttributePointer()))
                .map(attributes -> getModifyResult(context, nextRevision, command, thing, metadata))
                .orElseGet(() -> getCreateResult(context, nextRevision, command, thing, metadata));
    }

    @Override
    protected CompletionStage<ModifyAttribute> performWotValidation(final ModifyAttribute command,
            @Nullable final Thing previousThing,
            @Nullable final Thing previewThing
    ) {
        return wotThingModelValidator.validateThingAttribute(
                Optional.ofNullable(previousThing).flatMap(Thing::getDefinition).orElse(null),
                command.getAttributePointer(),
                command.getAttributeValue(),
                command.getResourcePath(),
                command.getDittoHeaders()
        ).thenApply(aVoid -> command);
    }

    private Result<ThingEvent<?>> getModifyResult(final Context<ThingId> context, final long nextRevision,
            final ModifyAttribute command, @Nullable final Thing thing, @Nullable final Metadata metadata) {

        final ThingId thingId = context.getState();
        final JsonPointer attributePointer = command.getAttributePointer();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        final CompletionStage<ModifyAttribute> validatedStage = buildValidatedStage(command, thing);
        final CompletionStage<ThingEvent<?>> eventStage = validatedStage.thenApply(modifyAttribute ->
                AttributeModified.of(thingId, attributePointer, modifyAttribute.getAttributeValue(), nextRevision,
                        getEventTimestamp(), dittoHeaders, metadata)
        );
        final CompletionStage<WithDittoHeaders> responseStage = validatedStage.thenApply(modifyAttribute ->
                appendETagHeaderIfProvided(modifyAttribute,
                        ModifyAttributeResponse.modified(thingId, attributePointer,
                                createCommandResponseDittoHeaders(dittoHeaders, nextRevision)), thing)
        );

        return ResultFactory.newMutationResult(command, eventStage, responseStage);
    }

    private Result<ThingEvent<?>> getCreateResult(final Context<ThingId> context, final long nextRevision,
            final ModifyAttribute command, @Nullable final Thing thing, @Nullable final Metadata metadata) {

        final ThingId thingId = context.getState();
        final JsonPointer attributePointer = command.getAttributePointer();
        final JsonValue attributeValue = command.getAttributeValue();
        final DittoHeaders dittoHeaders = command.getDittoHeaders();

        final CompletionStage<ModifyAttribute> validatedStage = buildValidatedStage(command, thing);
        final CompletionStage<ThingEvent<?>> eventStage = validatedStage.thenApply(modifyAttribute ->
                AttributeCreated.of(thingId, attributePointer, attributeValue, nextRevision, getEventTimestamp(),
                        dittoHeaders, metadata)
        );
        final CompletionStage<WithDittoHeaders> responseStage = validatedStage.thenApply(modifyAttribute ->
                appendETagHeaderIfProvided(modifyAttribute,
                        ModifyAttributeResponse.created(thingId, attributePointer, attributeValue,
                                createCommandResponseDittoHeaders(dittoHeaders, nextRevision)), thing)
        );

        return ResultFactory.newMutationResult(command, eventStage, responseStage);
    }

    @Override
    public Optional<EntityTag> previousEntityTag(final ModifyAttribute command, @Nullable final Thing previousEntity) {
        return Optional.ofNullable(previousEntity)
                .flatMap(Thing::getAttributes)
                .flatMap(attr -> attr.getValue(command.getAttributePointer()).flatMap(EntityTag::fromEntity));
    }

    @Override
    public Optional<EntityTag> nextEntityTag(final ModifyAttribute command, @Nullable final Thing newEntity) {
        return Optional.of(command.getAttributeValue()).flatMap(EntityTag::fromEntity);
    }
}
