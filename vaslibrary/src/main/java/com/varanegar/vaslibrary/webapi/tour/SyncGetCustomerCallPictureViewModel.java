package com.varanegar.vaslibrary.webapi.tour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/18/2017.
 */

public class SyncGetCustomerCallPictureViewModel {
    public UUID UniqueId ;
    public UUID CustomerCallUniqueId ;
    public UUID PictureSubjectUniqueId;
    public String NoPictureReason;
    public List<SyncGetCustomerCallPictureDetailViewModel> CustomerCallPictureDetails = new ArrayList<>();
}
