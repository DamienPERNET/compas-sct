// SPDX-FileCopyrightText: 2022 RTE FRANCE
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.sct.commons.scl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.compas.scl2007b4.model.*;
import org.lfenergy.compas.sct.commons.exception.ScdException;
import org.lfenergy.compas.sct.commons.util.PrivateEnum;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A representation of the <em><b>{@link PrivateService PrivateService}</b></em>.
 * <p>
 * The following features are supported:
 * </p>
 * <ol>
 *  <li>{@link PrivateService#getCompasPrivate(TPrivate, Class)
 *      <em>Returns the value of the <b>TPrivate </b> reference object By class type</em>}</li>
 *
 *  <li>{@link PrivateService#getCompasPrivates(TBaseElement, Class)
 *      <em>Returns the value of the <b>TPrivate </b> containment reference list from given <b>TBaseElement </b> By class type</em>}</li>
 *
 *  <li>{@link PrivateService#getCompasPrivates(List, Class)
 *      <em>Returns the value of the <b>TPrivate </b> containment reference list from given <b>TPrivate </b> elements By class type</em>}
 *   </li>
 * </ol>
 * @see org.lfenergy.compas.scl2007b4.model.TPrivate
 */
@Slf4j
public final class PrivateService {

    private PrivateService() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final ObjectFactory objectFactory = new ObjectFactory();

    public static <T> List<T> getCompasPrivates(List<TPrivate> tPrivates, Class<T> compasClass) throws ScdException {
        PrivateEnum privateEnum = PrivateEnum.fromClass(compasClass);
        List<Object> compasElements = tPrivates.stream().filter(tPrivate -> privateEnum.getPrivateType().equals(tPrivate.getType()))
            .map(TAnyContentFromOtherNamespace::getContent).flatMap(List::stream)
            .filter(JAXBElement.class::isInstance).map(JAXBElement.class::cast)
            .filter(Predicate.not(JAXBElement::isNil))
            .map(JAXBElement::getValue).collect(Collectors.toList());

        List<T> result = new ArrayList<>();
        for (Object compasElement : compasElements) {
            if (compasClass.isInstance(compasElement)) {
                result.add(compasClass.cast(compasElement));
            } else {
                throw new ScdException(String.format("Private is inconsistent. It has type=%s which expect JAXBElement<%s> content, " +
                        "but got JAXBElement<%s>",
                    privateEnum.getPrivateType(), privateEnum.getCompasClass().getName(), compasElement.getClass().getName()));
            }
        }
        return result;
    }

    public static <T> List<T> getCompasPrivates(TBaseElement baseElement, Class<T> compasClass) throws ScdException {
        if (!baseElement.isSetPrivate()) {
            return Collections.emptyList();
        }
        return getCompasPrivates(baseElement.getPrivate(), compasClass);
    }

    public static <T> Optional<T> getCompasPrivate(TPrivate tPrivate, Class<T> compasClass) throws ScdException {
        List<T> compasPrivates = getCompasPrivates(Collections.singletonList(tPrivate), compasClass);
        if (compasPrivates.size() > 1) {
            throw new ScdException(String.format("Expecting maximum 1 element of type %s in private %s, but got %d",
                compasClass.getName(), tPrivate.getType(), compasPrivates.size()));
        }
        if (compasPrivates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(compasPrivates.get(0));
    }

    public static Optional<TCompasICDHeader> getCompasICDHeader(TPrivate tPrivate) throws ScdException {
        return getCompasPrivate(tPrivate, TCompasICDHeader.class);
    }

    public static void removePrivates(TBaseElement baseElement, @NonNull PrivateEnum privateEnum) {
        if (baseElement.isSetPrivate()) {
            baseElement.getPrivate().removeIf(tPrivate -> privateEnum.getPrivateType().equals(tPrivate.getType()));
            if (baseElement.getPrivate().isEmpty()) {
                baseElement.unsetPrivate();
            }
        }
    }

    public static TPrivate createPrivate(TCompasBay compasBay) {
        return createPrivate(objectFactory.createBay(compasBay));
    }

    public static TPrivate createPrivate(TCompasCriteria compasCriteria) {
        return createPrivate(objectFactory.createCriteria(compasCriteria));
    }

    public static TPrivate createPrivate(TCompasFlow compasFlow) {
        return createPrivate(objectFactory.createFlow(compasFlow));
    }

    public static TPrivate createPrivate(TCompasFunction compasFunction) {
        return createPrivate(objectFactory.createFunction(compasFunction));
    }

    public static TPrivate createPrivate(TCompasICDHeader compasICDHeader) {
        return createPrivate(objectFactory.createICDHeader(compasICDHeader));
    }

    public static TPrivate createPrivate(TCompasLDevice compasLDevice) {
        return createPrivate(objectFactory.createLDevice(compasLDevice));
    }

    public static TPrivate createPrivate(TCompasSclFileType compasSclFileType) {
        return createPrivate(objectFactory.createSclFileType(compasSclFileType));
    }

    public static TPrivate createPrivate(TCompasSystemVersion compasSystemVersion) {
        return createPrivate(objectFactory.createSystemVersion(compasSystemVersion));
    }

    private static TPrivate createPrivate(JAXBElement<?> jaxbElement) {
        PrivateEnum privateEnum = PrivateEnum.fromClass(jaxbElement.getDeclaredType());
        TPrivate tPrivate = new TPrivate();
        tPrivate.setType(privateEnum.getPrivateType());
        tPrivate.getContent().add(jaxbElement);
        return tPrivate;
    }











    }
