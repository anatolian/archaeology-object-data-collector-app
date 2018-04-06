/**
 * @file  SampleCameraRemoteViewController.h
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "SampleCameraEventObserver.h"
#import "SampleStreamingDataManager.h"

@interface SampleCameraRemoteViewController
    : UIViewController <SampleEventObserverDelegate,
                        HttpAsynchronousRequestParserDelegate,
                        SampleStreamingDataDelegate, UIPickerViewDelegate,
                        UIPickerViewDataSource, UIGestureRecognizerDelegate>

- (IBAction)modeButton:(id)sender;
- (IBAction)actionButton:(id)sender;
- (IBAction)didTapZoomIn:(id)sender;
- (IBAction)didTapZoomOut:(id)sender;

@property (weak, nonatomic) IBOutlet UIButton *modeButtonText;
@property (weak, nonatomic) IBOutlet UIButton *actionButtonText;
@property (weak, nonatomic) IBOutlet UILabel *cameraStatusView;
@property (weak, nonatomic) IBOutlet UIView *sideView;
@property (weak, nonatomic) IBOutlet UIButton *zoomInButton;
@property (weak, nonatomic) IBOutlet UIButton *zoomOutButton;

@end
