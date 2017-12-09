// HTTP requester
package excavation.excavation_app.module.common.http;
public enum HTTPRequester
{
    GetArea("get_areas.php"),
    GetListings("get_listing.php"),
    Multiple("multiple_photo_upload.php"),
    AddMultiplePhotos("add_multiple_photo.php"),
    AddSamplePhoto("add_single_photo_workflow3.php"),
    AddSinglePhoto("add_single_photo.php"),
    AddContextNum("add_context.php"),
    GetImage("get_photo.php"),
    ReplacePhoto("replace_photo.php"),
    DeleteContext("delete_context.php"),
    GetProperty("get_property.php");
    private String fileName;
    /**
     * Constructor
     * @param file - file name
     */
    HTTPRequester(String file)
    {
        fileName = file;
    }

    /**
     * Get file name
     * @return Returns file name
     */
    public String getFileName()
    {
        return fileName;
    }
}
