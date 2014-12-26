package org.thinkbigthings.boot.domain;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * needs to be in the same package as the model you are associating with.
 *
 * http://docs.jboss.org/hibernate/core/4.0/hem/en-US/html/metamodel.html#metamodel-static
 * http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-part-four-jpa-criteria-queries/
 */
@StaticMetamodel(value = Sleep.class)
public class Sleep_ {
    public static volatile SingularAttribute<Sleep, Date> timeOutOfBed;
    public static volatile SingularAttribute<Sleep, User> user;
}
