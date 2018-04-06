/**
 * @file  SampleDateListViewController.h
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "HttpAsynchronousRequest.h"
#import "SampleCameraEventObserver.h"

@interface SampleDateListViewController
    : UIViewController <UITableViewDelegate, UITableViewDataSource,
                        HttpAsynchronousRequestParserDelegate,
                        SampleEventObserverDelegate>

@property (weak, nonatomic) IBOutlet UITableView *dateList;
@property (nonatomic) BOOL isMovieAvailable;

@end
