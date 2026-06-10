use crate::parser::{assemble, parse_program};

#[test]
fn test_factorial_parsing() {
    let input = "
            mov(0, 5)
            mov(1, 1)
            label(\"loop\")
            hlt()
        ";
    let (_, statements) = parse_program(input).unwrap();
    assert_eq!(statements.len(), 4);
    let bytecode = assemble(statements);
    // mov(0, 5) -> 0x00, 0, 5
    // mov(1, 1) -> 0x00, 1, 1
    // label("loop") -> at index 6
    // hlt() -> 0xFF
    assert_eq!(bytecode, vec![0x00, 0, 5, 0x00, 1, 1, 0xFF]);
}