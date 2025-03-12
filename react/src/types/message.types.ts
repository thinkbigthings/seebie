
enum MessageType {
    ASSISTANT = "ASSISTANT",
    USER = "USER",
}

interface MessageDto extends Record<string, unknown> {
    content: string,
    type: MessageType
}

export {MessageType}
export type {MessageDto}