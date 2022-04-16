package net.leozeballos.FastFood.address;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressTest {

    @Test
    void shouldCreateAddressWithBuilder() {
        // when
        Address address = Address.builder().id(1L).street("Carlos Pellegrini 1370").city("Argentina").build();

        // then
        assertEquals(1L, address.getId());
        assertEquals("Carlos Pellegrini 1370", address.getStreet());
        assertEquals("Argentina", address.getCity());
    }

    @Test
    void shouldCreateAddressUsingEmptyConstructor() {
        // when
        Address address = new Address();

        // then
        assertNull(address.getId());
        assertEquals("", address.getStreet());
        assertEquals("", address.getCity());
    }

    @Test
    void testHashCode() {
        // given
        Address address1 = Address.builder().street("Carlos Pellegrini 1370").city("Argentina").build();
        Address address2 = Address.builder().street("Carlos Pellegrini 1370").city("Argentina").build();

        // when
        int hashCode1 = address1.hashCode();
        int hashCode2 = address2.hashCode();

        // then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void canGetId() {
        // given
        Address address = Address.builder().id(1L).street("Carlos Pellegrini 1370").city("Argentina").build();

        // when
        Long id = address.getId();

        // then
        assertEquals(1L, id);
    }

    @Test
    void canGetStreet() {
        // given
        Address address = Address.builder().street("Carlos Pellegrini 1370").city("Argentina").build();

        // when
        String street = address.getStreet();

        // then
        assertEquals("Carlos Pellegrini 1370", street);
    }

    @Test
    void canGetCity() {
        // given
        Address address = Address.builder().street("Carlos Pellegrini 1370").city("Argentina").build();

        // when
        String city = address.getCity();

        // then
        assertEquals("Argentina", city);
    }

    @Test
    void canSetId() {
        // given
        Address address = Address.builder().build();

        // when
        address.setId(1L);

        // then
        assertEquals(1L, address.getId());
    }

    @Test
    void canSetStreet() {
        // given
        Address address = Address.builder().build();

        // when
        address.setStreet("Carlos Pellegrini 1370");

        // then
        assertEquals("Carlos Pellegrini 1370", address.getStreet());
    }

    @Test
    void canSetCity() {
        // given
        Address address = Address.builder().build();

        // when
        address.setCity("Argentina");

        // then
        assertEquals("Argentina", address.getCity());
    }

    @Test
    void testToString() {
        // given
        Address address = Address.builder().id(1L).street("Carlos Pellegrini 1370").city("Argentina").build();

        // when
        String toString = address.toString();

        // then
        assertEquals("Address(id=1, street=Carlos Pellegrini 1370, city=Argentina)", toString);
    }

}