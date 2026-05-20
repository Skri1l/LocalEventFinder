package com.local.event.finder.event;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

    public static Specification<Event> withFilters(
            EventFilterRequestDto filter,
            Integer userAge
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            /* Age restriction */
            predicates.add(
                    cb.lessThanOrEqualTo(
                            root.get("ageRestriction"),
                            userAge
                    )
            );

            /* CIty */
            if (filter.getCity() != null) {
                predicates.add(
                        cb.equal(root.get("city"), filter.getCity())
                );
            }

            /* Country */
            if (filter.getCountry() != null) {
                predicates.add(
                        cb.equal(root.get("country"), filter.getCountry())
                );
            }

            /* Category */
            if (filter.getCategoryId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("eventCategory").get("id"),
                                filter.getCategoryId()
                        )
                );
            }

            /* Tag */
            if (filter.getTagId() != null) {
                predicates.add(
                        cb.equal(
                                root.join("eventTags").get("id"),
                                filter.getTagId()
                        )
                );
            }

            /* Date from */
            if (filter.getDateFrom() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("startTime"),
                                filter.getDateFrom()
                        )
                );
            }

            /* Date to */
            if (filter.getDateTo() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("endTime"),
                                filter.getDateTo()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
