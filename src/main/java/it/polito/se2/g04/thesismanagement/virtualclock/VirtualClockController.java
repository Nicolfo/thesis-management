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

    private static int offset= 0;
    public static int getOffset(){
        return offset;
    }

    private static void setOffset(int offset){
        VirtualClockController.offset=offset;
    }
    @PostMapping("/API/virtualTimer/changeOffset/")
    public int changeTime(@RequestBody VirtualClockRequest virtualClockRequest){
        VirtualClockController.setOffset(virtualClockRequest.getOffset());
        proposalService.archiveExpiredProposals();
        return offset;
    }

}
