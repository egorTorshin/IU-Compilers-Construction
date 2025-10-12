package com.compiler;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%

%class Lexer
%public
%unicode
%line
%column
%cup
%{
    private ComplexSymbolFactory symbolFactory;

    public Lexer(java.io.Reader in, ComplexSymbolFactory sf) {
        this(in);
        this.symbolFactory = sf;
    }

    private Symbol symbol(String name, int sym) {
        return symbolFactory.newSymbol(name, sym, 
            new Location(yyline+1, yycolumn+1),
            new Location(yyline+1, yycolumn+yylength()));
    }

    private Symbol symbol(String name, int sym, Object val) {
        return symbolFactory.newSymbol(name, sym, 
            new Location(yyline+1, yycolumn+1),
            new Location(yyline+1, yycolumn+yylength()), val);
    }
%}

/* Lexical Rules */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Comment        = "//" [^\r\n]* {LineTerminator}?
Identifier     = [:jletter:] [:jletterdigit:]*
IntegerLiteral = 0 | [1-9][0-9]* | -[1-9][0-9]*
RealLiteral    = ({IntegerLiteral}\.[0-9]+)
StringLiteral  = \"([^\"\n\r\\]|\\[ntbrf\"\\])*\"

%%

/* Comments */
{Comment}      { /* ignore */ }

/* Keywords */
"var"       { return symbol("VAR", sym.VAR); }
"if"        { return symbol("IF", sym.IF); }
"else"      { return symbol("ELSE", sym.ELSE); }
"while"     { return symbol("WHILE", sym.WHILE); }
"for"       { return symbol("FOR", sym.FOR); }
"in"        { return symbol("IN", sym.IN); }
"routine"   { return symbol("ROUTINE", sym.ROUTINE); }
"type"      { return symbol("TYPE", sym.TYPE); }
"return"    { return symbol("RETURN", sym.RETURN); }
"true"      { return symbol("BOOLEAN_LITERAL", sym.BOOLEAN_LITERAL, true); }
"false"     { return symbol("BOOLEAN_LITERAL", sym.BOOLEAN_LITERAL, false); }
"end"       { return symbol("END", sym.END); }
"then"      { return symbol("THEN", sym.THEN); }
"loop"      { return symbol("LOOP", sym.LOOP); }
"print"     { return symbol("PRINT", sym.PRINT); }
"read"      { return symbol("READ", sym.READ); }
"is"        { return symbol("IS", sym.IS); }
"as"        { return symbol("AS", sym.AS); }
"reverse"   { return symbol("REVERSE", sym.REVERSE); }
"record"    { return symbol("RECORD", sym.RECORD); }
"array"     { return symbol("ARRAY", sym.ARRAY); }

/* Types */
"integer"   { return symbol("INTEGER", sym.INTEGER); }
"real"      { return symbol("REAL", sym.REAL); }
"float"     { return symbol("FLOAT", sym.FLOAT); }
"boolean"   { return symbol("BOOLEAN", sym.BOOLEAN); }
"string"    { return symbol("STRING", sym.STRING); }

/* Operators */
"+"         { return symbol("PLUS", sym.PLUS); }
"-"         { return symbol("MINUS", sym.MINUS); }
"*"         { return symbol("MULTIPLY", sym.MULTIPLY); }
"/"         { return symbol("DIVIDE", sym.DIVIDE); }
"="         { return symbol("EQUAL", sym.EQUAL); }
"<"         { return symbol("LESS", sym.LESS); }
">"         { return symbol("GREATER", sym.GREATER); }
"<="        { return symbol("LESS_OR_EQUAL", sym.LESS_OR_EQUAL); }
">="        { return symbol("GREATER_OR_EQUAL", sym.GREATER_OR_EQUAL); }
"!="        { return symbol("NOT_EQUAL", sym.NOT_EQUAL); }
"and"       { return symbol("AND", sym.AND); }
"or"        { return symbol("OR", sym.OR); }
"xor"       { return symbol("XOR", sym.XOR); }
"not"       { return symbol("NOT", sym.NOT); }
"%"         { return symbol("MOD", sym.MOD); }

/* Separators */
"("         { return symbol("LPAREN", sym.LPAREN); }
")"         { return symbol("RPAREN", sym.RPAREN); }
"["         { return symbol("LBRACKET", sym.LBRACKET); }
"]"         { return symbol("RBRACKET", sym.RBRACKET); }
"{"         { return symbol("LBRACE", sym.LBRACE); }
"}"         { return symbol("RBRACE", sym.RBRACE); }
";"         { return symbol("SEMICOLON", sym.SEMICOLON); }
":"         { return symbol("COLON", sym.COLON); }
":="        { return symbol("ASSIGN", sym.ASSIGN); }
"."         { return symbol("DOT", sym.DOT); }
".."        { return symbol("RANGE", sym.RANGE); }
","         { return symbol("COMMA", sym.COMMA); }

/* Literals */
{IntegerLiteral} { return symbol("INTEGER_LITERAL", sym.INTEGER_LITERAL, Integer.parseInt(yytext())); }
{RealLiteral}    { return symbol("REAL_LITERAL", sym.REAL_LITERAL, Double.parseDouble(yytext())); }
{StringLiteral}  { return symbol("STRING_LITERAL", sym.STRING_LITERAL, yytext()); }
{Identifier}     { return symbol("IDENTIFIER", sym.IDENTIFIER, yytext()); }

/* Whitespace */
{WhiteSpace}     { /* ignore */ }

/* Error fallback */
[^]              { throw new Error("Illegal character <"+ yytext()+">"); }

<<EOF>>          { return symbol("EOF", sym.EOF); }