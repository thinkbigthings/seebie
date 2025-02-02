
enum MessageType {
    ASSISTANT,
    USER
}

interface MessageDto {
    content: string,
    type: MessageType
}

export {MessageType}
export type {MessageDto}