package it.polito.se2.g04.thesismanagement.virtualclock;

import it.polito.se2.g04.thesismanagement.proposal.ProposalService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class VirtualClockController {
    private final ProposalService proposalService;

    public static int offset= 0;
    @PostMapping("/API/virtualTimer/changeOffset/")
    public int setOffset(@RequestBody VirtualClockRequest virtualClockRequest){
        System.out.println("time changed");
        offset= virtualClockRequest.getOffset();
        proposalService.archiveExpiredProposals();
        return offset;
    }

}
