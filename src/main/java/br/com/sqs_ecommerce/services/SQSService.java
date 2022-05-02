package br.com.sqs_ecommerce.services;

import java.time.LocalDateTime;
import java.util.List;

import javax.swing.MenuSelectionManager;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import com.google.gson.Gson;

import br.com.sqs_ecommerce.dto.InfMessage;

public class SQSService {

    public static void messageReader() {

        GetQueueUrlRequest request = configAws.getUrl();
        SqsClient sqsClient = configAws.sqsClient();
        GetQueueUrlResponse createResult = sqsClient.getQueueUrl(request);      

        // TODO - refatorar em outra classe esta regra de negócio...
        List<Message> messages = receiveMessages(sqsClient, createResult.queueUrl());
        
        for (Message mess : messages) {

            String myId = "pedidos";

            String jsonMess = mess.body();

            InfMessage pedido = new Gson().fromJson(jsonMess, InfMessage.class);
            String to = pedido.getTo();
            String from = pedido.getFrom();
            LocalDateTime now = LocalDateTime.now();

            //Monitorando as msg que estão sendo enviadas, so periodo de validacao.
            System.out.println("Mensagem recebida, from: " + pedido.getFrom() + " - to: " + pedido.getTo());

            if (to.equals(myId)) {
                switch (from) {
                    case "transacao":
                        System.out.println("[" + now + "][APROVADO TRANSACAO]" + " #Pedido: " + pedido.getNumPedido());
                        deleteMessages(sqsClient, createResult.queueUrl(), mess);
                        break;
                    case "notaFiscal":
                        System.out.println("[" + now + "][NF EMITIDA]" + " #Pedido: " + pedido.getNumPedido());
                        deleteMessages(sqsClient, createResult.queueUrl(), mess);
                        break;
                    case "estoque":
                        System.out.println("[" + now + "][ESTOQUE OK]" + " #Pedido: " + pedido.getNumPedido());
                        deleteMessages(sqsClient, createResult.queueUrl(), mess);
                        break;
                    case "despacho":
                        System.out.println("[" + now + "][TRANSPORTADORA]" + " #Pedido: " + pedido.getNumPedido());
                        deleteMessages(sqsClient, createResult.queueUrl(), mess);
                        // Só para gerar evento de tempo entre transporte e entrega.
                        pedido.setFrom(myId);
                        pedido.setTo(myId);
                        String jsonToPedido = new Gson().toJson(pedido);
                        sendMessage(sqsClient, createResult.queueUrl(), jsonToPedido);
                        break;
                    case "pedidos":
                        // este case é para simular entrega do pedido
                        System.out.println("[" + now + "][ENTREGUE]" + " #Pedido: " + pedido.getNumPedido());
                        deleteMessages(sqsClient, createResult.queueUrl(), mess);
                        break;
                    default:
                        break;
                }
            }           
        }

        sqsClient.close();
    }

    public static List<Message> receiveMessages(SqsClient sqsClient, String queueUrl) {        
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .maxNumberOfMessages(5)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        return messages;
    }

    public static void deleteMessages(SqsClient sqsClient, String queueUrl, Message message) {       
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }

    public static void sendMessage(SqsClient sqsClient, String queueUrl, String message) {             
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
    }
}