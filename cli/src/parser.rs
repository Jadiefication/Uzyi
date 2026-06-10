use std::collections::HashMap;
use nom::{
    IResult, Parser,
    branch::alt,
    bytes::complete::tag,
    character::complete::{alphanumeric1, multispace0, u8},
    combinator::{map, recognize},
    multi::{many0, many1},
    sequence::{delimited, preceded},
};
use nom::error::{Error, ErrorKind};

#[derive(Debug, Clone, PartialEq)]
pub enum Operand {
    Byte(u8),
    Label(String),
}

#[derive(Debug, Clone, PartialEq)]
pub enum Statement {
    Instruction(String, Vec<Operand>),
    Label(String),
}

fn parse_label_declaration(input: &str) -> IResult<&str, Statement> {
    let (input, _) = tag("label").parse(input)?;
    let (input, _) = multispace0.parse(input)?;
    let (input, name) = delimited(
        tag("(\""),
        recognize(many1(alt((alphanumeric1, tag("_"))))),
        tag("\")"),
    )
    .parse(input)?;
    Ok((input, Statement::Label(name.to_string())))
}

fn parse_operand(input: &str) -> IResult<&str, Operand> {
    alt((
        map(u8, Operand::Byte),
        map(
            delimited(
                tag("\""),
                recognize(many1(alt((alphanumeric1, tag("_"))))),
                tag("\""),
            ),
            |text: &str| Operand::Label(text.to_string()),
        ),
    ))
    .parse(input)
}

fn parse_instruction(input: &str) -> IResult<&str, Statement> {
    let (input, name) = recognize((alphanumeric1, many0(tag("_")))).parse(input)?;
    let (input, _) = multispace0.parse(input)?;
    let (input, operands) = delimited(
        tag("("),
        many0(preceded(
            multispace0,
            alt((
                map(
                    (parse_operand, preceded(multispace0, tag(","))),
                    |(op, _)| op,
                ),
                parse_operand,
            )),
        )),
        tag(")"),
    )
    .parse(input)?;
    Ok((input, Statement::Instruction(name.to_string(), operands)))
}

fn parse_comment(input: &str) -> IResult<&str, &str> {
    let (input, _) = tag("//").parse(input)?;
    let end_of_line = input.find('\n').unwrap_or(input.len());
    Ok((&input[end_of_line..], ""))
}

pub fn parse_program(input: &str) -> IResult<&str, Vec<Statement>> {
    let mut input = input;
    let mut statements = Vec::new();

    while !input.is_empty() {
        if let Ok((rem, _)) = multispace0::<&str, Error<&str>>(input) {
            if rem.len() < input.len() {
                input = rem;
                continue;
            }
        }

        if input.is_empty() {
            break;
        }

        if let Ok((rem, _)) = parse_comment(input) {
            input = rem;
            continue;
        }

        if let Ok((rem, stmt)) = parse_label_declaration(input) {
            input = rem;
            statements.push(stmt);
            continue;
        }

        if let Ok((rem, stmt)) = parse_instruction(input) {
            input = rem;
            statements.push(stmt);
            continue;
        }

        return Err(nom::Err::Error(Error::new(
            input,
            ErrorKind::Many0,
        )));
    }

    Ok(("", statements))
}

pub fn assemble(statements: Vec<Statement>) -> Vec<u8> {
    let mut labels = HashMap::new();
    let mut bytecode = Vec::new();

    let mut current_pos = 0;
    for stmt in &statements {
        match stmt {
            Statement::Label(name) => {
                labels.insert(name.clone(), current_pos as u8);
            }
            Statement::Instruction(_name, ops) => {
                current_pos += 1; // opcode
                current_pos += ops.len(); // operands
            }
        }
    }

    for stmt in statements {
        match stmt {
            Statement::Label(_) => {}
            Statement::Instruction(name, ops) => {
                let opcode = match name.as_str() {
                    "mov" => 0x00,
                    "movr" => 0x01,
                    "add" => 0x02,
                    "sub" => 0x03,
                    "mul" => 0x04,
                    "div" => 0x05,
                    "inc" => 0x06,
                    "dec" => 0x07,
                    "and" => 0x08,
                    "or" => 0x09,
                    "not" => 0x0A,
                    "xor" => 0x0B,
                    "shl" => 0x0C,
                    "shr" => 0x0D,
                    "load" => 0x0E,
                    "store" => 0x0F,
                    "cmp" => 0x10,
                    "beq" => 0x11,
                    "blo" => 0x12,
                    "bhi" => 0x13,
                    "bleq" => 0x14,
                    "bheq" => 0x15,
                    "b" => 0x16,
                    "push" => 0x17,
                    "pop" => 0x18,
                    "call" => 0x19,
                    "ret" => 0x1A,
                    "addi" => 0x1B,
                    "subi" => 0x1C,
                    "muli" => 0x1D,
                    "loadr" => 0x1E,
                    "storer" => 0x1F,
                    "sleep" => 0x20,
                    "hlt" => 0xFF,
                    _ => panic!("Unknown instruction: {}", name),
                };
                bytecode.push(opcode);
                for op in ops {
                    match op {
                        Operand::Byte(b) => bytecode.push(b),
                        Operand::Label(l) => {
                            let pos = *labels.get(&l).expect(&format!("Label not found: {}", l));
                            bytecode.push(pos);
                        }
                    }
                }
            }
        }
    }

    bytecode
}

