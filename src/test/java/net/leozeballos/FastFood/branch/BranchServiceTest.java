package net.leozeballos.FastFood.branch;

import net.leozeballos.FastFood.mapper.BranchMapper;
import net.leozeballos.FastFood.util.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock private BranchRepository branchRepository;
    @Mock private AuditService auditService;
    @Spy private BranchMapper branchMapper;
    private BranchService underTest;

    @BeforeEach
    void setUp() {
        underTest = new BranchService(branchRepository, branchMapper, auditService);
    }

    @Test
    void canFindAllBranches() {
        // when
        underTest.findAll();

        // then
        verify(branchRepository).findAll();
    }

    @Test
    void canFindBranchById() {
        // given
        Long id = 1L;
        Branch branch = new Branch();
        when(branchRepository.findById(id)).thenReturn(Optional.of(branch));

        // when
        underTest.findById(id);

        // then
        verify(branchRepository).findById(id);
    }

    @Test
    void canSaveBranch() {
        // given
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setName("Test Branch");
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        // when
        underTest.save(branch);

        // then
        verify(branchRepository).save(branch);
    }

    @Test
    void canDeleteBranch() {
        // given
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setName("Test Branch");

        // when
        underTest.delete(branch);

        // then
        ArgumentCaptor<Branch> argument = ArgumentCaptor.forClass(Branch.class);
        verify(branchRepository).delete(argument.capture());
        assertThat(argument.getValue()).isEqualTo(branch);
    }

    @Test
    void canDeleteBranchById() {
        // given
        Long id = 1L;
        Branch branch = new Branch();
        branch.setId(id);
        branch.setName("Test Branch");
        when(branchRepository.findById(id)).thenReturn(Optional.of(branch));

        // when
        underTest.deleteById(id);

        // then
        verify(branchRepository).deleteById(id);
    }

    @Test
    void canDeleteAllBranches() {
        // when
        underTest.deleteAll();

        // then
        verify(branchRepository).deleteAll();
    }
}
