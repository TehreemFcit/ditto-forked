/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.wot.validation;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nullable;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.things.model.Attributes;
import org.eclipse.ditto.things.model.Feature;
import org.eclipse.ditto.things.model.FeatureProperties;
import org.eclipse.ditto.things.model.Features;
import org.eclipse.ditto.things.model.Thing;
import org.eclipse.ditto.wot.model.ThingModel;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;

/**
 * Provides functionality to validate specific parts of a Ditto {@link Thing} and/or Ditto Thing {@link Features} and
 * single {@link Feature} instances.
 *
 * @since 3.6.0
 */
public interface WotThingModelValidation {

    /**
     * Validates the provided {@link Attributes} of a {@code Thing} based on the provided {@code ThingModel}.
     *
     * @param thingModel the ThingModel to validate against
     * @param attributes the attributes to validate
     * @param resourcePath the originating path of the command which caused validation
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateThingAttributes(ThingModel thingModel,
            @Nullable Attributes attributes,
            JsonPointer resourcePath,
            DittoHeaders dittoHeaders
    );

    /**
     * Validates the provided attribute at {@code attributePointer} having value {@code attributeValue} based on the
     * provided {@code ThingModel}.
     *
     * @param thingModel the ThingModel to validate against
     * @param attributePointer the attribute pointer (path) to validate
     * @param attributeValue the attribute value to validate
     * @param resourcePath the originating path of the command which caused validation
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateThingAttribute(ThingModel thingModel,
            JsonPointer attributePointer,
            JsonValue attributeValue,
            JsonPointer resourcePath,
            DittoHeaders dittoHeaders
    );

    /**
     * TODO TJ doc
     * @param thingModel
     * @param messageSubject
     * @param inputPayload
     * @param resourcePath
     * @param dittoHeaders
     * @return
     */
    CompletionStage<Void> validateThingMessageInput(ThingModel thingModel,
            String messageSubject,
            @Nullable JsonValue inputPayload,
            JsonPointer resourcePath,
            DittoHeaders dittoHeaders
    );

    /**
     * Validates the presence of the provided {@link Features} in the provided {@code featureThingModels} Map consisting
     * of all submodels of a Thing's {@code ThingModel}.
     *
     * @param featureThingModels a Map of submodels with their {@code instanceName} as key and their resolved
     * {@code ThingModel} as value
     * @param features the Features of a Thing to validate presence of the models in
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateFeaturesPresence(Map<String, ThingModel> featureThingModels,
            @Nullable Features features,
            DittoHeaders dittoHeaders
    );

    /**
     * Validates the {@code properties} of the provided {@link Features} against the passed {@code featureThingModels}.
     *
     * @param featureThingModels a Map of submodels with their {@code instanceName} as key and their resolved
     * {@code ThingModel} as value
     * @param features the Features of a Thing to validate the {@code properties} in
     * @param resourcePath the originating path of the command which caused validation
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateFeaturesProperties(Map<String, ThingModel> featureThingModels,
            @Nullable Features features,
            JsonPointer resourcePath,
            DittoHeaders dittoHeaders
    );

    /**
     * Validates the presence of the provided {@code feature} in the provided {@code featureThingModels} Map consisting
     * of all submodels of a Thing's {@code ThingModel}.
     *
     * @param featureThingModels a Map of submodels with their {@code instanceName} as key and their resolved
     * {@code ThingModel} as value
     * @param feature the Feature to validate presence of the models in
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateFeaturePresence(Map<String, ThingModel> featureThingModels,
            Feature feature,
            DittoHeaders dittoHeaders
    );

    /**
     * Validates the complete passed {@code feature} (properties and desired properties) based on the provided
     * {@code featureThingModel}.
     *
     * @param featureThingModel the feature's ThingModel to validate against
     * @param feature the Feature to validate
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateFeature(ThingModel featureThingModel,
            Feature feature,
            JsonPointer resourcePath,
            DittoHeaders dittoHeaders
    );

    /**
     * Validates the provided {@code featureProperties} (either being properties or desired properties based on the
     * passed {@code desiredProperties} flag) against the passed {@code featureThingModel}.
     *
     * @param featureThingModel the feature's ThingModel to validate against
     * @param featureId the feature's id to validate properties in
     * @param featureProperties the properties to validate
     * @param desiredProperties whether the provided {@code properties} are "desired" properties
     * @param resourcePath the originating path of the command which caused validation
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateFeatureProperties(ThingModel featureThingModel,
            String featureId,
            @Nullable FeatureProperties featureProperties,
            boolean desiredProperties,
            JsonPointer resourcePath,
            DittoHeaders dittoHeaders
    );

    /**
     * Validates the provided feature property at path {@code propertyPointer} and its value {@code propertyValue}
     * against the passed {@code featureThingModel}.
     *
     * @param featureThingModel the feature's ThingModel to validate against
     * @param featureId the feature's id to validate the property in
     * @param propertyPointer the feature property pointer (path) to validate
     * @param propertyValue the feature property value to validate
     * @param desiredProperty whether the provided feature property is a "desired" property
     * @param resourcePath the originating path of the command which caused validation
     * @param dittoHeaders the DittoHeaders to use in order to build a potential exception
     * @return a CompletionStage finished successfully with {@code null} or finished exceptionally in case of a
     * validation error - exceptionally finished with a {@link WotThingModelPayloadValidationException}
     */
    CompletionStage<Void> validateFeatureProperty(ThingModel featureThingModel,
            String featureId,
            JsonPointer propertyPointer,
            JsonValue propertyValue,
            boolean desiredProperty,
            JsonPointer resourcePath,
            DittoHeaders dittoHeaders
    );

    /**
     * Creates a new instance of WotThingModelValidation with the given {@code validationConfig}.
     *
     * @param validationConfig the WoT TM validation config to use.
     * @return the created WotThingModelValidation.
     */
    static WotThingModelValidation of(final TmValidationConfig validationConfig) {
        return new DefaultWotThingModelValidation(validationConfig);
    }
}
