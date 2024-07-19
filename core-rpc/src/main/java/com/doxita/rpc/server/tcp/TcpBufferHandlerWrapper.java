package com.doxita.rpc.server.tcp;

import com.doxita.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

// 缓冲区装饰器
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    
    private final RecordParser recordParser;
    
    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }
    
    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
    
    
    /**
     * 初始化记录解析器。
     * 该方法用于创建并配置一个RecordParser实例，该实例用于解析数据记录。解析器采用固定长度的模式，
     * 以处理具有固定头部长度的消息。解析器的输出被定向到一个处理程序，该处理程序负责进一步处理解析后的数据。
     *
     * @param bufferHandler 处理解析后数据的处理器。当解析器接收到足够数据后，它将调用这个处理器来处理数据。
     * @return 返回初始化后的RecordParser实例。
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler){
        // 创建一个新的RecordParser实例，设置固定长度模式，该长度为消息头的长度。
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        // 为parser设置输出处理程序。这个处理程序负责接收解析器解析的数据。
        parser.setOutput(new Handler<>() {
            // 用于存储消息体的预期长度。
            int size = -1;
            // 用于临时存储解析的数据。
            Buffer resultBuffer = Buffer.buffer();
            
            @Override
            public void handle(Buffer buffer) {
                // 当size为-1时，表示尚未读取到消息体的长度。此时，从buffer中读取消息体长度，
                // 并调整解析器的模式以适应消息体的长度。
                if (size == -1) {
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 已经读取到消息体长度的情况下，将收到的数据追加到resultBuffer中。
                    // 当有足够的数据来构成一个完整的消息时，调用bufferHandler来处理数据，
                    // 然后重置解析器的模式以处理新的消息头，同时清空resultBuffer以备下一个消息的解析。
                    resultBuffer.appendBuffer(buffer);
                    bufferHandler.handle(resultBuffer);
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }

}
