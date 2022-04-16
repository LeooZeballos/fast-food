package net.leozeballos.FastFood.address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock private AddressRepository addressRepository;
    private AddressService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AddressService(addressRepository);
    }

    @Test
    void canFindAllAddresses() {
        // when
        underTest.findAll();

        // then
        verify(addressRepository).findAll();
    }

    @Test
    void canFindAddressById() {
        // when
        underTest.findById(1L);

        // then
        verify(addressRepository).findById(1L);
    }

    @Test
    void canSaveAddress() {
        // given
        Address address = new Address();

        // when
        underTest.save(address);

        // then
        verify(addressRepository).save(address);
    }

    @Test
    void canDeleteAddress() {
        // given
        Address address = new Address();

        // when
        underTest.delete(address);

        // then
        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(address);
    }

    @Test
    void canDeleteAddressById() {
        // given
        Long id = 1L;

        // when
        underTest.deleteById(id);

        // then
        verify(addressRepository).deleteById(id);
    }

    @Test
    void canDeleteAllAddresses() {
        // when
        underTest.deleteAll();

        // then
        verify(addressRepository).deleteAll();
    }

}