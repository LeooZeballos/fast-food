package net.leozeballos.FastFood.branch;

import net.leozeballos.FastFood.address.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BranchTest {

    @Test
    void shouldCreateBranchWithBuilder() {
        // when
        Branch branch = Branch.builder()
                .id(1L)
                .name("Branch 1")
                .address(
                        Address.builder()
                                .id(1L)
                                .street("Carlos Pellegrini 1370")
                                .city("Argentina")
                                .build()
                )
                .build();

        // then
        assertEquals(1L, branch.getId());
        assertEquals("Branch 1", branch.getName());
        assertEquals("Carlos Pellegrini 1370", branch.getAddress().getStreet());
        assertEquals("Argentina", branch.getAddress().getCity());
    }

    @Test
    void shouldCreateBranchWithEmptyConstructor() {
        // when
        Branch branch = new Branch();

        // then
        assertNull(branch.getId());
        assertNull(branch.getName());
        assertNull(branch.getAddress());
    }

    @Test
    void testHashCode() {
        // given
        Branch branch1 = Branch.builder()
                .id(1L)
                .name("Branch 1")
                .address(
                        Address.builder()
                                .id(1L)
                                .street("Carlos Pellegrini 1370")
                                .city("Argentina")
                                .build()
                )
                .build();
        Branch branch2 = Branch.builder()
                .id(1L)
                .name("Branch 1")
                .address(
                        Address.builder()
                                .id(1L)
                                .street("Carlos Pellegrini 1370")
                                .city("Argentina")
                                .build()
                )
                .build();

        // when
        int hashCode1 = branch1.hashCode();
        int hashCode2 = branch2.hashCode();

        // then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void getId() {
        // given
        Branch branch = Branch.builder()
                .id(1L)
                .build();

        // when
        Long id = branch.getId();

        // then
        assertEquals(1L, id);
    }

    @Test
    void getName() {
        // given
        Branch branch = Branch.builder()
                .name("Branch 1")
                .build();

        // when
        String name = branch.getName();

        // then
        assertEquals("Branch 1", name);
    }

    @Test
    void getAddress() {
        // given
        Address address = Address.builder().build();
        Branch branch = Branch.builder()
                .address(address)
                .build();

        // when
        Address addressGet = branch.getAddress();

        // then
        assertEquals(address, addressGet);
    }

    @Test
    void setId() {
        // given
        Branch branch = Branch.builder().build();

        // when
        branch.setId(1L);

        // then
        assertEquals(1L, branch.getId());
    }

    @Test
    void setName() {
        // given
        Branch branch = Branch.builder().build();

        // when
        branch.setName("Branch 1");

        // then
        assertEquals("Branch 1", branch.getName());
    }

    @Test
    void setAddress() {
        // given
        Address address = Address.builder().build();
        Branch branch = Branch.builder().build();

        // when
        branch.setAddress(address);

        // then
        assertEquals(address, branch.getAddress());
    }

    @Test
    void testToString() {
        // given
        Branch branch = Branch.builder()
                .id(1L)
                .name("Branch 1")
                .address(
                        Address.builder()
                                .id(1L)
                                .street("Carlos Pellegrini 1370")
                                .city("Argentina")
                                .build()
                )
                .build();

        // when
        String toString = branch.toString();

        // then
        assertEquals("Branch(id=1, name=Branch 1, address=Address(id=1, street=Carlos Pellegrini 1370, city=Argentina))", toString);
    }

}