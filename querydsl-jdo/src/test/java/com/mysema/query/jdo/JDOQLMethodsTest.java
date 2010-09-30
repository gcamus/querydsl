/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.jdo;

import java.util.Arrays;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mysema.query.jdo.test.domain.Product;
import com.mysema.query.jdo.test.domain.QProduct;
import com.mysema.query.jdo.test.domain.QStore;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.path.CollectionPath;
import com.mysema.query.types.path.MapPath;

public class JDOQLMethodsTest extends AbstractJDOTest {

    private QProduct product = QProduct.product;

    private QStore store = QStore.store;

    @Test
    public void test(){
        Product p = query().from(product).limit(1).uniqueResult(product);
        for (BooleanExpression f : getFilters(
                product.name, product.description, "A0",
                store.products, p,
                store.productsByName, "A0", p,
                product.amount)){
            query().from(store, product).where(f).list(store, product);
        }
    }

    private <A,K,V> List<BooleanExpression> getFilters(
            StringExpression str, StringExpression other, String knownString,
            CollectionPath<A> list, A element,
            MapPath<K,V, ?> map, K key, V value,
            NumberExpression<Integer> number){
        return Arrays.<BooleanExpression>asList(
           // java.lang.String
           str.startsWith(knownString),
           str.endsWith(knownString),
           str.indexOf(knownString).gt(-1),
           str.indexOf(knownString, 1).gt(-1),
           str.substring(1).eq(knownString),
           str.substring(1,2).eq(knownString),
           str.lower().eq(knownString),
           str.upper().eq(knownString),
           str.matches(".*"),
           // java.util.Collection
           list.isEmpty(),
           list.isNotEmpty(),
           list.contains(element),
           list.size().gt(0),
           // java.util.Map
           map.isEmpty(),
           map.isNotEmpty(),
           map.containsKey(key),
           map.containsValue(value),
           map.get(key).eq(value),
           map.size().gt(0),
           number.abs().gt(0),
           number.sqrt().gt(0)
        );
    }

    @BeforeClass
    public static void doPersist() {
        // Persistence of a Product and a Book.
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            for (int i = 0; i < 10; i++) {
                pm.makePersistent(new Product("C" + i, "F" + i, i * 200.00, 2));
                pm.makePersistent(new Product("B" + i, "E" + i, i * 200.00, 4));
                pm.makePersistent(new Product("A" + i, "D" + i, i * 200.00, 6));
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
        System.out.println("");

    }
}
