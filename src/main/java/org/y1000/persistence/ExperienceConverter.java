package org.y1000.persistence;

import jakarta.persistence.AttributeConverter;
import org.y1000.exp.Experience;

public final class ExperienceConverter implements AttributeConverter<Experience, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Experience attribute) {
        return attribute.value();
    }

    @Override
    public Experience convertToEntityAttribute(Integer dbData) {
        return new Experience(dbData);
    }
}
