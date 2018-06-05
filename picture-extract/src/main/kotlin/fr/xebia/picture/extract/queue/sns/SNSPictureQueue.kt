package fr.xebia.picture.extract.queue.sns

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import fr.xebia.picture.extract.PictureRef
import fr.xebia.picture.extract.queue.PictureQueue

class SNSPictureQueue(private val topicArn: String) : PictureQueue {

    private val amazonSNS by lazy(AmazonSNSClientBuilder::defaultClient)

    override fun queue(pictureRef: PictureRef) {

        val msg = "${pictureRef.id}|${pictureRef.name}"

        val publishRequest = PublishRequest(topicArn, msg)
        val publishResult = amazonSNS.publish(publishRequest)

        println("MessageId - ${publishResult.messageId}")
    }
}
