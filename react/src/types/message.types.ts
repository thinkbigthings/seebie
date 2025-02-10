
enum MessageType {
    ASSISTANT = "ASSISTANT",
    USER = "USER",
}

function mapToMessageDto(raw: any): MessageDto {
    // Optionally, you can validate or transform the raw value:
    const typeValue = raw.type;
    if (!Object.values(MessageType).includes(typeValue)) {
        throw new Error(`Invalid message type: ${typeValue}`);
    }
    return {
        content: raw.content,
        type: typeValue as MessageType,
    };
}

interface MessageDto extends Record<string, unknown> {
    content: string,
    type: MessageType
}

export {MessageType, mapToMessageDto}
export type {MessageDto}